import React, {Component} from 'react';
import Api from "./Api";
import './Client.css';

export default class Client extends Component {
  constructor(props) {
    super(props);
    this.state = {speed: 'fast'};
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
        <div className="ClientCPU"> [{this.getCpuPercent()}%] </div>
        <div className="Timestamp">({this.state.now || '...'})</div>
      </div>
    );
  }

  switchSpeed() {
    this.setState({
      speed: {
        "fast": "medium",
        "medium": "slow",
        "slow": "fast"
      }[this.state.speed]
    })
  }

  reportCpu() {
    Api.reportCpu(
      {
        id: this.getId(),
        percent: this.getCpuPercent()
      },
      result => {
        this.setState({
          now: result.now
        });
      });
  }

  getCpuPercent() {
    return {
      "fast": 20,
      "medium": 80,
      "slow": 100
    }[this.state.speed];
  }

  getId() {
    return this.props.item.id;
  }
}
