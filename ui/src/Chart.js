import React, {Component} from 'react';
import CanvasJSReact, {CanvasJS} from './lib/canvasjs.react';
import Api from "./Api";

var CanvasJSChart = CanvasJSReact.CanvasJSChart;
var updateInterval = 500;

export default class Chart extends Component {

  constructor() {
    super();
    this.state = {clientList: []};
    this.addClientId = this.addClientId.bind(this);
    this.updateChart = this.updateChart.bind(this);
  }

  componentDidMount() {
    setInterval(this.updateChart, updateInterval);
  }

  addClientId(client) {
    const clientWithCpu = {...client, cpuAverage: 0};
    this.setState({
      clientList: [...this.state.clientList, clientWithCpu]
    });
  }

  async getDataPoints() {
    return this.state.clientList.reduce((listPromise, client) => {
      return listPromise.then( accumulator => {
        return Api.getCpuAverage(client, updatedClient => {
          const dataPoint = {
            label: "Client #" + updatedClient.id,
            y: updatedClient.cpuAverage
          };
          return [...accumulator, dataPoint]
        })
      })
    }, Promise.resolve([/* Initial value of accumulator */]));
  }

  getCpuGrandAverage(total, count) {
    return count === 0
      ? 0
      : Math.round(total / count)
  }

  updateChart() {
    this.getDataPoints().then(dataPoints => {
      var dpsTotal = 0;
      this.chart.options.data[0].dataPoints = dataPoints.map(datapoint => {
        const yValue = datapoint.y;
        const dataColor = yValue >= 90 ? "#e40000" : yValue >= 70 ? "#ec7426" : yValue >= 50 ? "#81c2ea" : "#88df86 ";
        dpsTotal += yValue;
        return {...datapoint, color: dataColor}
      });
      this.chart.options.title.text = "CPU Usage " + this.getCpuGrandAverage(dpsTotal, dataPoints.length) + "%";
      this.chart.render();
    })
  }

  render() {
    const options = {
      theme: "light",
      title: {
        text: "CPU Usage"
      },
      subtitles: [{
        text: "(Click React Logo above to add more Clients)"
      }],
      axisY: {
        title: "CPU Usage (%)",
        suffix: "%",
        maximum: 100
      },
      data: [{
        type: "column",
        yValueFormatString: "#,###'%'",
        indexLabel: "{y}",
        dataPoints: [
        ]
      }]
    };

    return (
      <div>
        <CanvasJSChart options={options} onRef={ref => this.chart = ref}
        />
      </div>
    );
  }
}
