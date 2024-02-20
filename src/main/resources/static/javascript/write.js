#(function(){
    $("#img").on("change", function(envent){
        var file = event.target.files[0];
        var reader = new FileReader();
        reader.onload=function(e){
            $("#preview").css("background", "url("+e.target.result+") no-repeat center");
            $("#preview").css("background-size","contain");
        }
        reader.readAsDataURL(file);
    });
});