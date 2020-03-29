import React, {Component} from 'react';
import Api from "./Api";
import Client from "./Client";

export default class ClientList extends Component {
  constructor(props) {
    super(props);
    this.state = {list: []};
  }

  addNewClient() {
    Api.newClientId(client => {
      this.setState({
        list: (this.state.list.concat(client))
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
      </div>
    );
  }
}
