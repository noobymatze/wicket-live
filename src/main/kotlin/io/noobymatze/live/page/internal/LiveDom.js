function _LiveDom_makeCallback(type, handlerId, sendToApp) {
    return function (event) {
        sendToApp(event, type, handlerId);
    }
}


function _LiveDom_applyEvents(element, events, sendToApp) {
    for (var key in events) {
        var handler = events[key];
        element.addEventListener(key, _LiveDom_makeCallback(key, handler, sendToApp));
    }
}


function _LiveDom_applyAttr(element, attributes) {
    for (var key in attributes) {
        var value = attributes[key];
        element.setAttribute(key, value);
    }
}


function _LiveDom_applyAttributes(element, attributes, sendToApp) {
    for (var key in attributes) {

        var value = attributes[key];

        key === 'EVENT'
            ? _LiveDom_applyEvents(element, value, sendToApp)
            :
        key === 'ATTRIBUTE'
            ? _LiveDom_applyAttr(element, value)
            : console.log('Unknown attribute type', key);
    }
}

function _LiveDom_render(node, sendToApp) {
    if (typeof node === 'string') {
        return document.createTextNode(node);
    }
    else {
        var name = node[0];
        var attributes = node[1];
        var element = document.createElement(name);
        _LiveDom_applyAttributes(element, attributes, sendToApp);
        for (var i = 2; i < node.length; ++i) {
            var child = node[i];
            element.appendChild(_LiveDom_render(child, sendToApp))
        }

        return element;
    }
}
