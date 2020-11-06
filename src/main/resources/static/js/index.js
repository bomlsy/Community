$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
    //点击发布帖子之后，发布帖子的弹出框隐藏
	$("#publishModal").modal("hide");

	//向服务器发送异步请求
	//获取标题和内容
	var title = $("#recipient-name").val(); //和index.html中标题的id保持一致
	var content = $("#message-text").val();
	//发送异步请求
	$.post(
	    "/community/discuss/add", //访问路径
	    {"title":title,"content":content}, //传入的参数
	    function(data) { //回调函数
	        data = $.parseJSON(data);
	        //在提示框中显示msg
	        $("#hintBody").text(data.msg);
            //显示提示框
	        $("#hintModal").modal("show");
	        //2秒后自动隐藏提示框
            setTimeout(function(){
                $("#hintModal").modal("hide");
                //发布帖子成功，刷新页面
                if(data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
	    }
	);
}