window.addEventListener('load', function () {
    'use strict';


    // CONSTANTS

    var EVENT = "EVENT";
    var ATTRIBUTE = "ATTRIBUTE";

    var app = document.getElementById('app');



    // RENDER


    function makeCallback(key, handlerId) {
        return function (event) {
            Wicket.WebSocket.send(JSON.stringify({
                handlerId: handlerId,
                payload: null
            }));
        }
    }


    function applyEvents(element, events) {
        for (var key in events) {
            var handler = events[key];
            element.addEventListener(key, makeCallback(key, handler));
        }
    }


    function applyAttr(element, attributes) {
        for (var key in attributes) {
            var value = attributes[key];
            element.setAttribute(key, value);
        }
    }


    function applyAttributes(element, attributes) {
        for (var key in attributes) {

            var value = attributes[key];

            key === 'EVENT'
                ? applyEvents(element, value)
                :
            key === 'ATTRIBUTE'
                ? applyAttr(element, value)
                : console.log('Unknown attribute type', key);
        }
    }

    function render(node) {
        if (typeof node === 'string') {
            return document.createTextNode(node);
        }
        else {
            var name = node[0];
            var attributes = node[1];
            var element = document.createElement(name);
            applyAttributes(element, attributes);
            for (var i = 2; i < node.length; ++i) {
                var child = node[i];
                element.appendChild(render(child))
            }

            return element;
        }
    }

    Wicket.Event.subscribe("/websocket/open", function () {
        console.log('Opened')
    });

    Wicket.Event.subscribe("/websocket/message", function (event, message) {
        console.log('Message: ', message)
        var result = JSON.parse(message);
        var node = render(result);
        morphdom(app, node);

    });

    Wicket.Event.subscribe("/websocket/closed", function () {
        console.log('Closed')
    });

    Wicket.Event.subscribe("/websocket/error", function () {
        console.log('Error')
    });
});