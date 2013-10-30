var rollback = function() {
	$.ajax({
		url: 'api/longTx/rollback',
		success: function() {
			location.reload();
		},
	});
}

var commit = function() {
	$.ajax({
		url: 'api/longTx/commit',
		success: function() {
			alert("Committed successfully!");
			location.reload();
		},
		error: function() {
			alert("Sorry, could not commit :(");
			location.reload();
		}
	});
}

$.getJSON("api/notes", function(data) { 
    // Now use this data to update your view models, 
    // and Knockout will update your UI automatically 
    var ViewModel = function() {
    	var self = this;
    	this.data = data;
    	this.editMe = function(element) {
			$.ajax({
			  url: 'api/notes/edit/' + element.id + '/' + element.contents,
			  success: function() {
			  	    location.reload();
			  },
			});
    	};
    	this.removeMe = function(element) {
			$.ajax({
			  url: 'api/notes/remove/' + element.id,
			  success: function() {
			  	    location.reload();
			  },
			});
    	};
    	this.create = function() {
			$.ajax({
				url: 'api/longTx/create/' + self.newTxName(),
				success: function() {
					location.reload();
				},
			});
	    };
	    this.hideIt = function(element) {
	    	$.ajax({
				url: 'api/longTx/delete/' + element.id,
				success: function() {
					location.reload();
				},
			});
	    }
	    this.select = function(element) {
			$.ajax({
				url: 'api/longTx/select/' + element.id,
				success: function() {
					location.reload();
				},
			});
	    }
    	this.newTxName = ko.observable('');
    }
    ko.applyBindings(new ViewModel());
})

