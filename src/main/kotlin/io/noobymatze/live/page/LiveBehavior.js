window.addEventListener('load', function () {
    'use strict';

    // CONSTANTS

    var LIVE_DIFF_COMMAND = 0;


    var app = document.getElementById('app');

    function sendToApp(event, type, handlerId, payload) {
        Wicket.WebSocket.send(JSON.stringify({
            handlerId: handlerId,
            payload: payload
        }));
    }


    function serializeForm(form) {
        var formData = new FormData(form);

        var params = {};
        formData.forEach(function (value, key) {
            params[key] = value;
        });

        return params;
    }

    window.addEventListener('submit', function (e) {
        e.preventDefault()
        var handler = e.target.getAttribute('data-wicket-submit');
        if (!handler) {
            return
        }

        sendToApp(e, 'submit', Number(handler), {
            type: 'form',
            data: serializeForm(e.target)
        });
    });

    window.addEventListener('click', function (e) {
        var handler = e.target.getAttribute('data-wicket-click');
        if (handler) {
            sendToApp(e, 'click', Number(handler));
        }
    });

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