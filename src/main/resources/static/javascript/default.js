   $(function(){
       $('.loginUser:first-child').on('click', function() {

             $('.loginUser:not(:first-child)').show();

             // 클릭 이벤트 중복 방지를 위해 이벤트 리스너 제거
                 $(this).off('click');
             });

    });
