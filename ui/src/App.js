import React, {Component} from 'react';

import reactLogo from './images/react.svg';
import Api from "./Api";

import './App.css';
import ClientList from "./ClientList";

export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = {title: ''};
    this.addClient = this.addClient.bind(this);
  }

  addClient() {
    this.refs.clientList.addNewClient();
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
        <header><h1>Welcome to the {this.state.title}</h1></header>
        <div onClick={this.addClient}>
          <img width="200" height="200" src={reactLogo} className="App-logo" alt="React Logo"/>
        </div>
        <ClientList ref="clientList" key={"Clients"} />
      </div>
    );
  }
}
