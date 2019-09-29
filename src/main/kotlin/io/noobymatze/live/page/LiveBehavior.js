window.addEventListener('load', function () {
    'use strict';

    var app = document.getElementById('app');

    function applyAttributes(element, attributes) {
        for (var i = 0; i < attributes.length; i++) {
            var attr = attributes[i];
            if (attr.type === 'Attr') {
                element.setAttribute(attr.key, attr.value);
            }
            else if (attr.type === 'Event') {
                element.addEventListener(attr.event, function (event) {
                    Wicket.WebSocket.send(JSON.stringify({
                        handlerId: attr.handler,
                        payload: null
                    }));
                });
            }
            else {
                console.debug('Unkown Attribute: ', attr);
            }
        }
    }

    function render(node) {
        if (node === undefined) {
            return;
        }

        if (node.type === 'Node') {
            var element = document.createElement(node.name)
            applyAttributes(element, node.attributes);
            node.children.map(function (next) {
                element.appendChild(render(next))
            });
            return element
        }
        else if(node.type === 'Text') {
            return document.createTextNode(node.content);
        }
        else {
            console.debug('Unkown Html node', node);
        }
    }

    Wicket.Event.subscribe("/websocket/open", function () {
        console.log('Opened')
    });

    Wicket.Event.subscribe("/websocket/message", function (event, message) {
        var result = JSON.parse(message);
        var node = render(result);
        morphdom(app, node);

        console.log('Message: ', message)
    });

    Wicket.Event.subscribe("/websocket/closed", function () {
        console.log('Closed')
    });

    Wicket.Event.subscribe("/websocket/error", function () {
        console.log('Error')
    });
});