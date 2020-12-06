# SensorExample

### 将安卓手机搭建成为一个服务器，并对外提供访问传感器数据的接口

##### 读取数据示例
 

    JavaScript：var ws = new WebSocket(“ws://192.168.0.1:7999/data”);  
    
    ws.onmessage = function(evt){
       console.log(evt);
    };

##### json格式示例
    {"data":{"record":"2.6939886,0.057609074,0.029736657","spu":100,"type":"orientation"},
      "from":"172.6.0.112"}
      
     from表示数据来源ip地址，data中record表示这次数据的记录，若记录是矢量，则数据采集之后是  
     三维的采用逗号分隔开，若记录是标量，则是一维的，spu表示数据采集间隔，单位为毫秒，type表
     示该条数据来源于哪种类型的传感器，该例子中，表示方向。

##### 控制传感器示例
    var obj = {"device":["192.168.0.1", "192.168.0.2"],
                "delay":1000,
                "sensor":["orientation", "gravity"],
                "spu":1000};
    var httpRequest = new XMLHttpRequest();
    httpRequest.open('POST', 'url', true); 
    httpRequest.setRequestHeader("Content-type","application/json");
    httpRequest.send(JSON.stringify(obj));
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState == 4 && httpRequest.status == 200) {
            var json = httpRequest.responseText;
            console.log(json);
        }
    };
    该条命令表示以1000毫秒作为传感器数据采集速率，在1000毫秒后开启192.
    168.0.1和192.169.0.2两台（确保这两台手机都开启了服务器，并且处于同
    一局域网）设备上的orientation和gravity两种传感器。
