$(document).ready(function() {
	Pleasure.init();
	Layout.init();
	UserPages.login();
	$("#signUpForm input").focus(function() {
		$("#signUpAlertDiv").hide();
	});
	$("#resetForm input").focus(function() {
		$("#resetFormAlertDiv").hide();
	});
	
});

//sendRegSmsCodeBtn
$(document).on("click", "#sendRegSmsCodeBtn", function() {
	var username = $("#registerUsername").val();
	if(username =='' || !username.match("^1[0-9]{10}")){
		$("#signUpAlertDiv").html(usernameFormatError).show();
	}else{
		$.ajax({
			url : sendRegSmsCodeUrl + username,
			type : "POST",
			success : function(data) {
				if (data.success) {
					$("#sendRegSmsCodeBtn").html(successShowText);
				} else {
					$("#sendRegSmsCodeBtn").html(usernameError);
				}
				$("#sendRegSmsCodeBtn").attr("disabled","true");
			}
		});
	}
});

//sendRegSmsCodeBtn提示信息更改
$(document).on("focus","#registerUsername",function(){
	$("#sendRegSmsCodeBtn").removeAttr("disabled");
	$("#sendRegSmsCodeBtn").html(getSmsCode);
});

	

$("#signUpForm").on("submit", function() {
	if ($("#passwordRegisterOne").val() != $("#passwordRegisterTwo").val()) {
		$("#signUpAlertDiv").html(passwordError).show();
		return false;	
	}
	$.ajax({
		url : registerUrl,
		type : "POST",
		data : $("#signUpForm").serialize(),
		success : function(data) {
			if (data.success) {				
				$('.show-pane-login').click();
				$("#success").html(successRegisterText).fadeIn(2000);
				$("#success").html(successRegisterText).fadeOut(1500);
			} else {
				$("#signUpAlertDiv").html(registerErrorText).show();
			}
		}
	});
	return false;
});
//忘记密码部分
//验证码
$(document).on("click", "#sendResetSmsCodeBtn", function() {
	var resetUsername = $("#resetUsername").val();
	if (resetUsername =='' || !resetUsername.match("^1[0-9]{10}")) {
		$("#resetFormAlertDiv").html(usernameFormatError).show();	
	}else{
		$.ajax({
			url : sendFindPwSmsCodeUrl + resetUsername,
			type : "POST",
			success : function(data) {
				if (data.success) {
					$("#sendResetSmsCodeBtn").html(successShowText);
				} else {
					$("#sendResetSmsCodeBtn").html(accountNotExistError);
				}
				$("#sendResetSmsCodeBtn").attr("disabled","true");
			}
		});
	}
});

$(document).on("focus","#resetUsername",function(){
	$("#sendResetSmsCodeBtn").removeAttr("disabled");
	$("#sendResetSmsCodeBtn").html(getSmsCode);
});

$("#resetForm").on("submit", function() {
	if ($("#resetPasswordOne").val() != $("#resetPasswordTwo").val()) {
		$("#resetFormAlertDiv").html(passwordError).show();
		return false;
	}
	$.ajax({
		url : resetPasswordUrl,
		type : "POST",
		data : $("#resetForm").serialize(),
		success : function(data) {
			if (data.success) {
				$('.show-pane-login').click();
				$("#success").html(successResetPw).fadeIn(2000);
				$("#success").html(successResetPw).fadeOut(1500);
			} else {
				$("#resetFormAlertDiv").html(resetPwError).show();
			}
		}
	});
	return false;
	// 阻止表单自动提交事件
});