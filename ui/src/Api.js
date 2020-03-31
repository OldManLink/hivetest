/* eslint-disable no-undef */
function getSummary(cb) {
  return fetch('/api/summary', {
    accept: "application/json"
  })
    .then(checkStatus)
    .then(parseJSON)
    .then(cb);
}

/* eslint-disable no-undef */
function newClientId(cb) {
  return fetch('/api/newid', {
    accept: "application/json"
  })
    .then(checkStatus)
    .then(parseJSON)
    .then(cb);
}

/* eslint-disable no-undef */
function reportCpu(report, cb) {
  return fetch('/api/report', {
    method: 'POST',
    headers: {
      'Accept': "application/json",
      'Content-Type': 'application/json',
      'Csrf-Token' :'nocheck'
    },
    body: JSON.stringify(report)
  })
    .then(checkStatus)
    .then(parseJSON)
    .then(cb);
}

/* eslint-disable no-undef */
function getCpuAverage(client, cb) {
  return fetch('/api/cpu/' + client.id, {
    accept: "application/json"
  })
    .then(checkStatus)
    .then(parseJSON)
    .then(cb);
}

function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }
  const error = new Error(`HTTP Error ${response.statusText}`);
  error.status = response.statusText;
  error.response = response;
  console.log(error); // eslint-disable-line no-console
  throw error;
}

function parseJSON(response) {
  return response.json();
}

const Api = { getSummary, newClientId, reportCpu, getCpuAverage };
export default Api;
