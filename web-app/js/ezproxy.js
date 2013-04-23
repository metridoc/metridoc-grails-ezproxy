$('#ezproxyData tbody td').each(function () {
    $this = $(this);
    var titleVal = $this.text();
    if (titleVal != '') {
        $this.attr('title', titleVal);
    }
});

editor = CodeMirror.fromTextArea(document.getElementById('code'), {
    mode: 'groovy',
    lineNumbers: true,
    matchBrackets: true
});

