var userEditUrl="/adminUser/edit/";
var addRoleUrl="/adminUser/addRole";
var removeRoleUrl="/adminUser/removeRole";
var userEnableddUrl="/adminUser/enabled"

	
function userEdit(aObj){
	var userId = $(aObj).attr("data-id");
	jQuery.ajax({
		type : 'POST',
		url : userEditUrl+userId,
		success : function(data, textStatus) {
			$(".modal-content").html(data);
			$('#userEditModal').modal('show')
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
		}
	});
}

function update(){
	jQuery.ajax({
		type : 'POST',
		url : "/adminUser/update",
		data: $("#userForm").serialize(),
		success : function(data, textStatus) {
			if(data.success){
				alert("更新成功");
				history.go(0);
			}else{
				alert(data.errorMsg);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
		}
	});
}


function addAdmin(){
	var username = $("#addAdminInput").val();
	if(confirm("请确定将用户"+username+"添加到管理员？")){
		jQuery.ajax({
			type : 'POST',
			url : "/adminUser/addAdmin",
			data:{username:username},
			success : function(data, textStatus) {
				if(data.success){
					alert("设置成功");
					history.go(0);
				}else{
					alert(data.errorMsg);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		});
	}
}
function removeAdmin(obj){
	var username = $(obj).attr("data-username");
	if(confirm("请确定将用户"+username+"取消管理员权限？")){
		jQuery.ajax({
			type : 'POST',
			url : "/adminUser/removeAdmin",
			data:{username:username},
			success : function(data, textStatus) {
				if(data.success){
					alert("设置成功");
					history.go(0);
				}else{
					alert(data.errorMsg);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		});
	}
}


function changeRole(radioObj){
	var requestUrl = "";
	if(radioObj.checked==true){
		requestUrl = addRoleUrl;
	}else{
		requestUrl = removeRoleUrl;
	}
	var userId = $("#userId").val();
	var roleId = $(radioObj).attr("data-id");
	jQuery.ajax({
		type : 'POST',
		url : requestUrl,
		data:{userId:userId,roleId:roleId},
		success : function(data, textStatus) {
			if(data=="success"){
			}else{
				alert("保存数据出错！请联系客服！");
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
		}
	});
}


function userEnabled(checkedObj){
	var userId = $("#userId").val();
	var enabled= document.getElementById("userEnabledCheckbox").checked;
	//enabled的值和checkbox相反
	enabled=!enabled;
	jQuery.ajax({
		type : 'POST',
		url : userEnableddUrl,
		data:{userId:userId,enabled:enabled},
		success : function(data, textStatus) {
			if(data=="success"){
				var enabledJq = $("#userEnabled"+userId)
				enabledJq.attr("data-enabled",enabled)
				if(enabled==true){
					enabledJq.html("正常")
				}else{
					enabledJq.html("禁用")
				}
			}else{
				alert("保存数据出错！请联系客服！");
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
		}
	});
	
}




function changeList(obj){
	aObj = $(obj)
	var max = aObj.attr('data-max');
	var offset =  aObj.attr('data-offset');
	$("#max").val(max);
	$("#offset").val(offset);
	var searchStr = $("#searchStr").val();
	searchStr = searchStr.replace(/(^\s*)|(\s*$)/g,'')
	if(searchStr==''){
		searchStr  = "a"
	}
	if(searchStr == ""){
		searchStr="a"
	}
	var newUrl = "/adminUser/list/"+$("#type").val()+"/"+$("#max").val()+"/"+$("#offset").val()+"/"+searchStr;
	window.location.href = newUrl;
}
function searchUser(){
	var searchStr = $("#searchStr").val();
	searchStr = searchStr.replace(/(^\s*)|(\s*$)/g,'')
	if(searchStr==''){
		searchStr  = "a"
	}
	if(searchStr == ""){
		searchStr="a"
	}
	var newUrl = "/adminUser/list/"+$("#type").val()+"/"+$("#max").val()+"/"+$("#offset").val()+"/"+searchStr;
	window.location.href = newUrl;
}