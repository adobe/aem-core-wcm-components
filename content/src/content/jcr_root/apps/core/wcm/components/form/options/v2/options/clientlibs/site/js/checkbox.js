$(document).ready(function () {
    $(document).on('click', 'button.cmp-form-button', function() {		  
        $('form .cmp-form-option').each(function(e){
            //$(this).find('fieldset');
            var errorMsg = $(this).attr('data-cmp-required-message');	
            //console.log(errorMsg);
            if($(this).find('fieldset').attr('required') != undefined){
                var elemName = $('input:first', this).attr('name');
                //console.log("Name -- " + elemName);
                var fieldset = $(this).find('fieldset');
                var firstElement = $('input:first', this);
                if (($("input[name*=" + elemName + "]:checked").length)<=0) {
                    firstElement.on('invalid', function(){ firstElement.get(0).setCustomValidity(errorMsg); });
                    //fieldset.on('invalid', function(){ fieldset.setCustomValidity(errorMsg); });
                    return false;  
                }else{
                    firstElement.get(0).setCustomValidity('');
                    firstElement.removeAttr('required');	
                    return true;
                }				
            }

        });
    });	
});	    