$(function() {
    $(".code-box").each(function(index) {
            $(this).resizable({
                handles: "se"
            });
        }
    )

    $("#ezproxyEditors").buttonset()



});



$('#ezproxyData tbody td').each(function(index){
    $this = $(this);
    var titleVal = $this.text();
    if (titleVal != '') {
        $this.attr('title', titleVal);
    }
});

