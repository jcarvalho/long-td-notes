var create = function() {
	$.ajax({
		url: 'api/longTx/create',
		success: function() {
			location.reload();
		},
	});
}

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
    var viewModel = {
    	'data': data,
    	'editMe': function(element) {
			$.ajax({
			  url: 'api/notes/edit/' + element.id + '/' + element.contents,
			  success: function() {
			  	    location.reload();
			  },
			});
    	},
    	'removeMe': function(element) {
    		if(!confirm("Are you sure?")) {
    			return;
    		}
			$.ajax({
			  url: 'api/notes/remove/' + element.id,
			  success: function() {
			  	    location.reload();
			  },
			});
    	}
    }
    ko.applyBindings(viewModel);
})

