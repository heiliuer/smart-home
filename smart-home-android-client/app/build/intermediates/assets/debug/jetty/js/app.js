/**
 * Created by Heiliuer on 2016/5/7 0007.
 */
Vue.filter("encodeURI", function (value) {
    return encodeURI(value)
});
var vm = new Vue({
    el: "#vue-app",
    data: {
        status: {
            deskLightOn: true,
            floorLightOn: true
        },
        isConnected: false,
        waitEcho: false,
        currentUrl: location.href
    },
    methods: {
        switchLight: function () {
            this.waitEcho = true;
            // console.log(this.status.deskLightOn);
            if (this.isConnected) {
                socket.send(JSON.stringify(this.status));
            }
        }
    }
});

var socket;
function openSocket() {
    var host = "192.168.1.147:8081";
// var host = location.host;
// 创建一个Socket实例
    socket = new WebSocket('ws://' + host + "/" + "wsctrl");
// 打开Socket
    socket.onopen = function (event) {
        console.log("isConnected true")
        vm.isConnected = true;

        // 发送一个初始化消息
        //socket.send('I am the client and I\'m listening!');
        // 关闭Socket....
        //socket.close()
    };

// 监听消息
    socket.onmessage = function (event) {
        //console.log('Client received a message data ', event.data);
        try {
            var data = JSON.parse(event.data);
            if ("deskLightOn" in data) {
                vm.status = data;
                vm.waitEcho = false;
            } else {
                console.log(event.data, " is invalid format!");
            }
        } catch (e) {
            console.log(e);
        }

    };

// 监听Socket的关闭
    socket.onclose = function (event) {
        vm.isConnected = false;
        setTimeReOpenSocket();
        console.log('Client notified socket has closed', event);
    };

    socket.onerror = function (event) {
        vm.isConnected = false;
        setTimeReOpenSocket();
        console.log('Client notified socket has errored', event);
    }
}

var setTimeReOpenSocket = function () {
    var timer;
    return function () {
        if (timer != null) {
            clearTimeout(timer);
        }
        setTimeout(openSocket, 3000);
    }
}();

openSocket();

$("#vue-app").fadeIn(300);

$("#qrcode .weui_btn_dialog").click(function () {
    $("#qrcode").hide()
});

$("#qrcode_switcher").click(function () {
    $("#qrcode").show();
})

