(function(document, $, Coral) {
    "use strict";

    $(document).on("click", ".cq-dialog-submit", function(e) {
    	$(".dialog-style-system__hidden").empty();
        $(".dialog-style-system__item input:checked").each(function(i, element){
        	var parent = element.parentNode;
        	var styleId = parent.id;
			var styleIdInput = $('<input/>').attr({type: 'hidden', name: './cq:styleIds', value: styleId});
			$(".dialog-style-system__hidden").append(styleIdInput);
        });
    });

})(document, Granite.$, Coral);
