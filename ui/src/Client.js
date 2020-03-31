import React, {Component} from 'react';
import Api from "./Api";
import './Client.css';

export default class Client extends Component {

  constructor(props) {
    super(props);
    this.state = {speed: 'fast', sequence: 0, pendingLogs: []};
    this.reportCpu = this.reportCpu.bind(this);
    this.switchSpeed = this.switchSpeed.bind(this);
  }

  async componentDidMount() {
    this.clockInterval = setInterval(this.reportCpu, 1000);
  }

  componentWillUnmount() {
    clearInterval(this.clockInterval);
  }

  render() {
    return (
      <div className={"Client Speed-" + this.state.speed} onClick={this.switchSpeed}>
        <div className="ClientId">Client #{this.getId()}</div>
        <div className="ClientCPU"> [{this.getCpuPercent()}%]</div>
        <div className="Timestamp">({this.state.now || '...'})</div>
        {
          this.state.pendingLogs.length === 0
            ? null
            : <div className="Pending">{this.getPendingDots()}</div>
        }
      </div>
    );
  }

  switchSpeed() {
    this.setState({
      speed: {
        "fast": "medium",
        "medium": "slow",
        "slow": "blocked",
        "blocked": "fast",
      }[this.state.speed]
    })
  }

  reportCpu() {
    const report = this.getReport();
    if (["fast", "medium"].includes(this.state.speed)) {
      while (this.state.pendingLogs.length > 0) {
        const report = this.state.pendingLogs[0];
        this.sendReport(report);
        this.setState({
          pendingLogs: this.state.pendingLogs.filter(item => item.sequence !== report.sequence)
        })
      }
      this.sendReport(report);
    } else {
      if (this.state.speed === "blocked") {
        this.savePendingReport(report);
      }
    }
  }

  sendReport(report) {
    Api.reportCpu(report,
      result => {
        this.setState({
          now: result.now
        });
      });
  }

  savePendingReport(report) {
    this.setState({
      pendingLogs: [...this.state.pendingLogs, report]
    });
  }

  getPendingDots() {
    return this.state.pendingLogs.map(log => '.').concat('');
  }

  getReport() {
    return {
      id: this.getId(),
      sequence: this.getSequence(),
      percent: this.getCpuPercent()
    }
  }

  getId() {
    return this.props.item.id;
  }

  getSequence() {
    const current = this.state.sequence;
    this.setState({
      sequence: current + 1
    });
    return current;
  }

  getCpuPercent() {
    return {
      "fast": 25,
      "medium": 75,
      "slow": 100,
      "blocked": 25,
    }[this.state.speed];
  }
}
