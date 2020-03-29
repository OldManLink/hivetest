import React, {Component} from 'react';
import Api from "./Api";
import Client from "./Client";

export default class ClientList extends Component {

  state = {list: []};

  addNewClient() {
    Api.newClientId(client => {
      this.setState({
        list: [ ...this.state.list, client ]
      });
    });

  }

  render() {
    return (
      <div className="ClientList">
        <ul>
          {(this.state.list || []).map(item => (
            <Client key={item.id} item={item} />
          ))}
        </ul>
        <div>
          { this.state.list.length === 0
              ? "(Click React Logo To Add Clients)"
              : "(Click Client to change CPU percentage)"
          }
        </div>
      </div>
    );
  }
}
