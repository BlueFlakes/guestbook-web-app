$('form').submit(function(){
    var input = $('#test').val();
    if(input === ''){
        $('#test').val('empty');
    }
});