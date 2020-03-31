import React, {Component} from 'react';

import reactLogo from './images/react.svg';
import Api from "./Api";

import './App.css';
import ClientList from "./ClientList";
import Chart from "./Chart";

export default class App extends Component {

  constructor(props) {
    super(props);
    this.state = {title: ''};
    this.addClient = this.addClient.bind(this);
  }

  addClient() {
    this.refs.clientList.addNewClient(this.refs.cpuChart.addClientId);
  }

  async componentDidMount() {
    Api.getSummary(summary => {
      this.setState({
        title: summary.content
      });
    });
  }

  render() {
    return (
      <div className="App">
        <header><h1>{this.state.title.length === 0
          ? "Loading, please wait..."
          : "Welcome to the " + this.state.title
        }</h1></header>
        <div onClick={this.addClient}>
          <img width="200" height="200" src={reactLogo} className="App-logo" alt="React Logo"/>
        </div>
        {this.state.title.length === 0
          ? null
          : <Chart ref="cpuChart"/>
        }
        <ClientList ref="clientList"/>
      </div>
    );
  }
}
