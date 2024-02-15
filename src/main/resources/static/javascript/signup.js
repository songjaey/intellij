
$(function(){
    $(".step1").css("color","#222");

//    $("#allAgree").on("change",function(){
//        var isChk = $(this).is(":checked");
//        if(isChk){
//            $("input[name=accept]").prop('checked',true);
//            $("join_btn").addClass("on");
//        }else{
//            $("input[name=accept]").prop('checked',false);
//            $("#join_btn").removeClass("on");
//        }
//    })

// 약관 동의
     function updateAllAgreeCheckbox() {
             var allCheckboxes = $("input[name=accept]");
             $("#allAgree").prop("checked", allCheckboxes.length === allCheckboxes.filter(":checked").length);
         }

         // Event listener for individual checkboxes
         $("input[name=accept]").on("change", function () {
             updateAllAgreeCheckbox();
             updateSubmitButtonState(); // Call function to update submit button state
         });

         // Event listener for "전체 동의합니다." checkbox
         $("#allAgree").on("change", function () {
             var isChk = $(this).is(":checked");
             $("input[name=accept]").prop("checked", isChk);
             $("#join_btn").toggleClass("on", isChk);
             updateSubmitButtonState(); // Call function to update submit button state
         });

         // Event listener for submit button click
         $("#join_btn").on("click", function () {
             if ($(this).hasClass("on")) {
                 agreeBt();
             }
         });

         // Function to update the state of the submit button
         function updateSubmitButtonState() {
             var isAgree1Checked = $("#agree1:checked").length > 0;
             var isAgree2Checked = $("#agree2:checked").length > 0;
             var isAgree3Checked = $("#agree3:checked").length > 0;

             var areAllRequiredAgreed = isAgree1Checked && isAgree2Checked && isAgree3Checked;

             $("#join_btn").toggleClass("on", areAllRequiredAgreed);
         }

     // 가입 양식
     $(".write_list input").on("keyup",function(){
        var isValue=true;
        $(".write_list input").each(function(i,v){
            if( $(v).val()=='' ){
                isValue=false;
                return;
            }
        });

        if(isValue){
            $("#submit_btn").addClass("on");
        }else{
            $("#submit_btn").removeClass("on");
        }
     });



});
function agreeBt(){
    var hasOn = $("#join_btn").hasClass("on");
    if(!hasOn){
        alert("필수 약관 동의 체크하세요");
    }else{
        $("#signup_wrap").show();
        $("#agree_wrap").hide();
        $(".step1").css("color","#b7b7b7");
        $(".step2").css("color","#222");
    }
}
function submit(){
    var hasOn = $("#submit_btn").hasClass("on");
    if(hasOn){
        $("#signFm").submit();
    }else{
        alert("양식을 모두 입력하세요");
    }
}



