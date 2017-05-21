'use strict';

import {GetRequest} from "components/modules/common/common";

import consts from 'configs/const.config';

/**
 * Socket
 *
 */
//angular
//    .module('pt')
//    .factory('websocket', webSocketFunc);

function webSocketFunc() {

    var webSocket = function () {
        this.colletion = [];
        this.ws = null;
        this.url = null;
    };

    webSocket.prototype.initWebSocket = function (urlParameter) {
        // var sign = GetRequest(urlParameter)['sign'];
        // window.WebSocket = window.WebSocket || window.MozWebSocket;

        // if (window.WebSocket) {
        //     this.url = consts.WEB_SOCKET_URL + '/' + urlParameter;
        //     this.ws = new WebSocket(this.url);
        // } else {
        //     //this.url = WEB_SOCKET_URL + '/sockjs/' + urlParameter;
        //     this.url = consts.WEB_SOCKET_URL + '/sockjs/dataSourceWebSocketHandler';
        //     this.ws = new SockJS(this.url, undefined, {
        //         protocols_whitelist: []
        //     });
        // }
        // this.ws.onopen = function () {
        //     console.log(this);
        //     console.log('Info: WebSocket connection opened.');
        //     if (!window.WebSocket) {
        //         this.send("SocketJS:" + sign);
        //     }
        // };
        // this.ws.onmessage = function (event) {
        //     console.log('Received: ' + event.data);
        //     this.colletion.push(event.data)
        // };
        // this.ws.onclose = function (event) {
        //     console.log('Info: WebSocket connection closed.');
        //     console.log(event);
        // };
        return this.ws;
    };

    webSocket.prototype.on = function (webSocket) {
        var message = '';
        this.ws.onmessage = function (event) {
            console.log('Received: ' + event.data);
            message = event.data;
        };
        return message;
    };

    webSocket.prototype.disconnect = function () {
        console.log(this);
        console.log('disconnect');
        if (this.ws != null) {
            console.log('Info: WebSocket connection closed.');
            this.ws.close();
            this.ws = null;
        }
    };

    webSocket.prototype.send = function (message) {
        if (this.ws != null) {
            console.log('Sent: ' + message);
            this.ws.send(message);
        } else {
            alert('connection not established, please connect.');
        }
    };

    return webSocket;
}

export default webSocketFunc;
