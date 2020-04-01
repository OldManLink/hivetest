import React, {Component} from 'react';
import Api from "./Api";
import Client from "./Client";

export default class ClientList extends Component {

  constructor(props) {
    super(props);
    this.state = {list: []};
  }

  addNewClient(cb) {
    Api.newClientId(client => {
      this.setState({
        list: [...this.state.list, client]
      });
      cb(client)
    });
  }

  render() {
    return (
      <div className="ClientList">
        <ul>
          {(this.state.list || []).map(item => (
            <Client key={item.id} item={item}/>
          ))}
        </ul>
        <div>
          {this.state.list.length === 0
            ? ""
            : "(Move mouse over Client to change CPU percentage and block)"
          }
        </div>
      </div>
    );
  }
}
