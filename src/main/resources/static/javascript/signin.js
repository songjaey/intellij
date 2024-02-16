$(function(){

    chkSaveId();

});

function loginForm(){

    saveId();

    $("#signFm").submit();
}
function saveId(){
    var exdate = new Date();
    var isChk = $("#save").is(":checked");
    if(isChk){
        exdate.setTime(exdate.getTime() + 1000 *3600*24*30 );
    }else{
        exdate.setTime(exdate.getTime() - 1000 *3600*24*30 );
    }
    var id = $("#email").val()
    document.cookie = "saveid="+escape(id)+"; path=/; expires="+exdate+";";
}

function chkSaveId(){
    var cookid = getCookie("saveid");
    if(cookid !=""){
        $("#email").val(cookid);
        $("#save").prop("checked",true);
    }
}

function getCookie(Name){
    var search = Name+"=";

    if(document.cookie.length >0 ){
        offset=document.cookie.indexOf(search);
        if(offset != -1){
            offset += search.length;
            endIndex = document.cookie.indexOf(";",offset);
            if(endIndex == -1){
                endIndex = document.cookie.length;
            }
            return unescape(document.cookie.substring(offset, endIndex));
        }
    }
    return "";
}