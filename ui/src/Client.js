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
        <h2>Client #{this.getId()} ({this.state.now})</h2>
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
    const percent = {
      "fast": 20,
      "medium": 80,
      "slow": 100
    }[this.state.speed];
    const report = {id: this.getId(), percent: percent};
    Api.reportCpu(report, result => {
      this.setState({
        now: result.now
      });
    });
  }

  getId() {
    return this.props.item.id;
  }
}
