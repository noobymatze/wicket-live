window.addEventListener('load', function () {
    'use strict';


    // CONSTANTS

    var app = document.getElementById('app');

    function sendToApp(event, type, handlerId) {
        Wicket.WebSocket.send(JSON.stringify({
            handlerId: handlerId,
            payload: null
        }));
    }


    // RENDER


    Wicket.Event.subscribe("/websocket/open", function () {
        console.log('Opened')
    });

    Wicket.Event.subscribe("/websocket/message", function (event, message) {
        console.log('Message: ', message)
        var result = JSON.parse(message);
        var node = _LiveDom_render(result, sendToApp);
        morphdom(app, node);

    });

    Wicket.Event.subscribe("/websocket/closed", function () {
        console.log('Closed')
    });

    Wicket.Event.subscribe("/websocket/error", function () {
        console.log('Error')
    });
});