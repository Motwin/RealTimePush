var ValueList = {
  RED_COLOR: '#F5A9A9',
  GREEN_COLOR: '#A9F5BC',
  /**
   * Insert a new line in the list
   */
  insertLine: function(position, properties) {
    if(position == 0) {
      this._selectList().prepend(this._createLine(properties));
    } else {
      this._selectLineAtPosition(position - 1).after(this._createLine(properties));
    }
  },
  /**
   * Update a specific line in the list
   */
  updateLine: function(position, updatedProperties) {
    var line = this._selectLineAtPosition(position),
        priceSpan = line.find('span.price'),
        oldPrice = parseInt(priceSpan.text());
        
    // update each property of the line
    for(var property in updatedProperties) {
      line.find('span.' + property).text(updatedProperties[property]);
    }
    
    // flash the line in red or green depending of the price
    var newPrice = parseInt(priceSpan.text());
    if(oldPrice > newPrice) {
      this._flashBackground(position, this.RED_COLOR);
    } else if(oldPrice < newPrice) {
      this._flashBackground(position, this.GREEN_COLOR);
    }
  },
  /**
   * Move a line from a position to another position
   */
  moveLine: function(oldPosition, newPosition) {
    if(newPosition > oldPosition) {
      this._selectLineAtPosition(newPosition).after(this._selectLineAtPosition(oldPosition));
    } else if(newPosition < oldPosition) {
      this._selectLineAtPosition(newPosition).before(this._selectLineAtPosition(oldPosition));
    }
  },
  /**
   * Delete a specific line in the list
   */
  deleteLine: function(position) {
    this._selectLineAtPosition(position).remove();
  },
  /**
   * build a new list line using the provided properties
   */
  _createLine: function(properties) {
    return '<li>' +
           '  <span class="title">' + properties['title'] + '</span>' +
           '  <span class="price">' + properties['price'] + '</span>' +
           '</li>';
  },
  /**
   * Select the list in the DOM
   */
  _selectList: function() {
    return $('ul');
  },
  /**
   * Select a existing line
   */
  _selectLineAtPosition: function(position) {
    return $('ul li').eq(position);
  },
  /**
   * Change the background color of an element
   */
  _flashBackground: function(position, color) {
    this._selectLineAtPosition(position)
            .stop()
            .css('background-color', color)
            .delay(250)
            .queue(function() {
              $(this).css('background-color', '');
            });
  }
};

$(document).ready(function() {
  
	// set the SDK log level (use DEBUG in development environment and ERROR in production environment)
  motwin.Logger.setLevel(motwin.Logger.DEBUG);
	
	// create the ClientChannel
	var channel = motwin.createClientChannel("http://tests.motwin.net:1249", {
		appName:'realTimePush',
		appVersion:'3.3'
	});
	
  // connect or create the manager that manages the lifecycle (cordova only)
  if(typeof cordova === "undefined") {
    channel.connect();
  } else {
    channel.useCordovaLifeCycle();
  }

  // open a continuous query and manage the callbacks 
  var query = channel.createContinuousQuery("select title,price from RealTimePush order by price");
  
  query.onInsert(function(metadata, position, properties) {
    ValueList.insertLine(position, properties);
  });
  
  query.onUpdate(function(metadata, position, properties, updatedProperties) {
    ValueList.updateLine(position, updatedProperties);
  });
  
  query.onMove(function(metadata, oldPosition, newPosition) {
     ValueList.moveLine(oldPosition, newPosition);
  });
  
  query.onDelete(function(metadata, position) {
   ValueList.deleteLine(position);
  });
    
  query.onError(function(error) {
    alert(error.message);
  });
    
  query.start();

});