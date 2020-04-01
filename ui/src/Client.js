import React, {Component} from 'react';
import Api from "./Api";
import './Client.css';

export default class Client extends Component {

  constructor(props) {
    super(props);
    this.state = {cpu: 20, sequence: 0, pendingLogs: [], hover: false, blocked: false};
    this.reportCpu = this.reportCpu.bind(this);
    this.mouseIn = this.mouseIn.bind(this);
    this.mouseOut = this.mouseOut.bind(this);
    this.toggleBlocked = this.toggleBlocked.bind(this);
    this.updateCpu = this.updateCpu.bind(this);
  }

  async componentDidMount() {
    this.clockInterval = setInterval(this.reportCpu, 1000);
  }

  componentWillUnmount() {
    clearInterval(this.clockInterval);
  }

  mouseIn() {
    this.setState({hover: true})
  }

  mouseOut() {
    this.setState({hover: false})
  }

  toggleBlocked() {
    this.setState({blocked: !this.state.blocked})
  }

  updateCpu(event) {
    this.setState({cpu: parseInt(event.target.value)})
  }

  render() {
    return (
      <div className={"Client Speed-" + this.getSpeed() + (this.state.blocked ? " Blocked" : "") }
           onMouseEnter={this.mouseIn}
           onMouseLeave={this.mouseOut}>
        <div className="ClientId">Client #{this.getId()}</div>
        {
          !this.state.hover
            ? <div className="ClientCPU"> [{this.getCpuPercent()}%]</div>
            : <div className="ClientCPU"> &nbsp;[
              <input className="ClientCPU" type="number"
                     value={this.state.cpu}
                     min="0"
                     max="100"
                     onChange={this.updateCpu}/>%]
            </div>
        }
        <div className="Timestamp">({this.state.now || '...'})</div>
        {
          !this.state.hover
            ? null
            : <div className="Checkbox"><label>
              <input type="checkbox"
                     checked={this.state.blocked}
                     onChange={this.toggleBlocked}/> block</label>
            </div>
        }
        {
          this.state.pendingLogs.length === 0
            ? null
            : <div className="Pending">{this.getPendingDots()}</div>
        }
      </div>
    );
  }

  getSpeed() {
    const cpuVal = this.state.cpu;
    return cpuVal >= 90 ? "snail"
      : cpuVal >= 75 ? "slow"
        : cpuVal >= 50 ? "sluggish"
          : cpuVal >= 25 ? "medium"
            : "fast"
  }

  reportCpu() {
    const report = this.getReport();
    if (this.state.cpu < 100 && !this.state.blocked) {
      while (this.state.pendingLogs.length > 0) {
        const report = this.state.pendingLogs[0];
        this.sendReport(report);
        this.setState({
          pendingLogs: this.state.pendingLogs.filter(item => item.sequence !== report.sequence)
        })
      }
      this.sendReport(report);
    } else {
      if (this.state.cpu < 100 && this.state.blocked) {
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
    return this.state.cpu;
  }
}
