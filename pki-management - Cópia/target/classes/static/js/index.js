
$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});


$('button[name="remove_levels"]').on('click', function(e) {
	  var $form = $(this).closest('deleteCreditorForm');
	  e.preventDefault();
	  $('#confirmRemove').modal({
	      backdrop: 'static',
	      keyboard: false
	    })
	    .one('click', '#delete', function(e) {
	      $form.trigger('submit');
	    });
	});