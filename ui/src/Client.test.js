import React from 'react';
import ReactDOM from 'react-dom';

import Client from './Client';
import Api from "./Api";

jest.mock('../src/Api');

describe('App tests', () => {

  it('renders without crashing', () => {
    const div = document.createElement('div');
    const item = {id: 42};
    ReactDOM.render(<Client item = {item}/>, div);
    ReactDOM.unmountComponentAtNode(div);
  });

  it('newClientId function should called exactly once', () => {
    expect(Api.newClientId.mock.calls.length).toBe(0);
  });
});
