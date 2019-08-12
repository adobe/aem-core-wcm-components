(function($document, author) {
    var openContentFragment = {
        icon: 'browse',
        text: 'Open content fragment',
        handler: function(editable, param, target) {
            var hostName = window.location.host,
                currentContentFragmentUrl = window.location.protocol + "//" + hostName + editable.path + ".-1.json";
            $.getJSON(currentContentFragmentUrl, function(result) {
                var fragmentUrl = window.location.protocol + "//" + hostName,
                    resultFragmentPath = editable.type === "api/components/content/contentfragment" ? "/editor.html" + result.fragmentPath : "/assets.html" + result.parentPath;
                if(result.fragmentPath || result.parentPath){
                	fragmentUrl = fragmentUrl + resultFragmentPath;
                    window.open(fragmentUrl, "_blank");
                }
            });
        },
        condition: function(editable) {
            return editable.type === "api/components/content/contentfragment" || editable.type === "api/components/content/contentfragmentlist";
        },
        isNonMulti: true
    };

    $document.on('cq-layer-activated', function(ev) {
        if (ev.layer === 'Edit') {
            author.EditorFrame.editableToolbar.registerAction('EAEM_OPEN_DIALOG', openContentFragment);
        }
    });

    //This logic is work around when 'cq-layer-activated' is not triggered
    $(document).ready(checkContainer);

    function checkContainer() {
        if ($("#EditableToolbar").length && author.EditorFrame.editableToolbar) {
            author.EditorFrame.editableToolbar.registerAction('EAEM_OPEN_DIALOG', openContentFragment);
        } else {
            setTimeout(checkContainer, 50); //wait 50 ms, then try again
        }
    }

})($(document), Granite.author);