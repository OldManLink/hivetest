import React, {Component} from 'react';
import CanvasJSReact, {CanvasJS} from './lib/canvasjs.react';

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
    const clientWithCpu = {...client, cpuAverage: 0}
    this.setState({
      clientList: [...this.state.clientList, clientWithCpu]
    });
    console.log(this.state.clientList)
  }

  updateChart() {
    var dpsColor, dpsTotal = 0, deltaY, yVal;
    var dps = this.chart.options.data[0].dataPoints;
    var chart = this.chart;
    for (var i = 0; i < dps.length; i++) {
      deltaY = Math.round(2 + Math.random() * (-2 - 2));
      yVal = deltaY + dps[i].y > 0 ? (deltaY + dps[i].y < 100 ? dps[i].y + deltaY : 100) : 0;
      dpsColor = yVal >= 90 ? "#e40000" : yVal >= 70 ? "#ec7426" : yVal >= 50 ? "#81c2ea" : "#88df86 ";
      dps[i] = {label: "Core " + (i + 1), y: yVal, color: dpsColor};
      dpsTotal += yVal;
    }
    chart.options.data[0].dataPoints = dps;
    chart.options.title.text = "CPU Usage " + Math.round(dpsTotal / 6) + "%";
    chart.render();
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
          {label: "Core 1", y: 68},
          {label: "Core 2", y: 3},
          {label: "Core 3", y: 8},
          {label: "Core 4", y: 87},
          {label: "Core 5", y: 2},
          {label: "Core 6", y: 6}
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
