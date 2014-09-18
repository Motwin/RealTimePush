(function(motwin, window, undefined ) {

var LifeCycleManager = function(clientChannel) {
    var connected = false;
    document.addEventListener("deviceready", function() { 
        // first connect
        if(!connected) {
            connected = true;
            clientChannel.connect();
        }
        // connect channel when resume
        document.addEventListener("resume", function() {
            if(!connected) {
                connected = true;
                clientChannel.connect();
            }
        }, false);
        // disconnect channel when pause
        document.addEventListener("pause", function() {
            if(connected) {
                connected = false;
                clientChannel.disconnect();
            }
        }, false);
        window.addEventListener('unload', function() {
            if(connected) {
                connected = false;
                clientChannel.disconnect();
            }
        }, false); 
    }, false);
}

motwin.extendClientChannel("useCordovaLifeCycle", function() {
    LifeCycleManager(this);
    return this;
});
    
})( motwin, window );