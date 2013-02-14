$('#ezproxyData tbody td').each(function(index){
    $this = $(this);
    var titleVal = $this.text();
    if (titleVal != '') {
        $this.attr('title', titleVal);
    }
});

