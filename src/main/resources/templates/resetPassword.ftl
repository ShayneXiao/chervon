<!DOCTYPE html>
<!-- saved from url=(0038)https://egopowerplus.com/account/login -->
<html lang="en" class="customers login fa-events-icons-ready">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

		<meta name="robots" content="index,follow">
		<meta name="google-site-verification" content="uiBaq8oS5tFh1KbnHMDVVbnVAEtLe9Pn5knG_xiaL70">
		<meta name="google-site-verification" content="dBdznvsxW1ZG9ZLJrupCD7i--Pl0YCUACDgtI-y55kQ">
		<meta name="msvalidate.01" content="2DA5E6771AE5E98B4F41E427BFD4F8FD">
		<title> Account | EGO POWER+ </title>
		<meta name="description" content="">
		<meta name="author" content="EGO POWER+">

		<!-- Mobile Specific Metas -->
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

		<!-- Stylesheets -->
		<link href="/Account _ EGO POWER+_files/styles.css" rel="stylesheet" type="text/css" media="all">
		<link href="/Account _ EGO POWER+_files/styles-new.scss.css" rel="stylesheet" type="text/css" media="all">
		<link href="/Account _ EGO POWER+_files/mediaqueries.css" rel="stylesheet" type="text/css" media="all">

		<!--[if lte IE 8]>
      <link href="//cdn.shopify.com/s/files/1/0262/2513/t/40/assets/ie.css?12262124082151192973" rel="stylesheet" type="text/css" media="all" />
      <![endif]-->

		<!-- Icons -->
		<link rel="shortcut icon" type="image/x-icon" href="https://cdn.shopify.com/s/files/1/0262/2513/t/40/assets/favicon.ico?12262124082151192973">

		<!-- Custom Fonts -->
		<link href="/Account _ EGO POWER+_files/css" rel="stylesheet" type="text/css">

		<!--[if lt IE 7]>
        <script src="//cdn.shopify.com/s/shopify/json2.js" type="text/javascript"></script>
        <![endif]-->

		<!-- jQuery and jQuery fallback -->
		<script src="/Account _ EGO POWER+_files/jquery.min.js.下载"></script>
		<script>
			function resetPassword() {
				var pwd = $("#password").val();
				if(pwd == "" || pwd == null) {
					alert("password cannot be empty !!!");
					$("#resetPwdForm").reset();
					return false;
				}
				var confirmpwd = $("#password_confirmation").val();
				if(confirmpwd == "" || confirmpwd == null) {
					alert("password_confirmation cannot be empty !!!");
					$("#resetPwdForm").reset();
					return false;
				}
				if(pwd != confirmpwd) {
					alert("password is not same with password_confirmation !!!");
					$("#resetPwdForm").reset();
					return false;
				}
				var formdata = {
					"type": $("#type").val(),
					"id": $("id").val(),
					"attributes": {
						"password": pwd,
						"password_confirmation": confirmpwd
					}
				}
				$("#resetPwdForm").ajaxSubmit({
					url: "/api/v1/resets",
					headers: {
						Content - Type: "application / vnd.api + json",
						Accept: "application / vnd.api + json",
						Authorization: $("#token").val()
					},
					type: "patch",
					data: {
						data: formdata
					},
					success: function(data){
						$("#content").innerHTML = data;
					},
					error: function(error){
						alert(error);
					}
				});
			}
		</script>

		<!-- end -->

		<link rel="stylesheet" type="text/css" href="/Account _ EGO POWER+_files/spr-05d8adfa7bfbbb65c5fb55b0659d8fa6f7d297faa9930816634a789969c02a13.css" media="screen">
		<div style="width: 1px; height: 1px; display: inline; position: absolute;"><img height="1" width="1" style="border-style:none;" alt="" src="/Account _ EGO POWER+_files/out">
		</div>
		<div style="width: 1px; height: 1px; display: inline; position: absolute;"><img height="1" width="1" style="border-style:none;" alt="" src="/Account _ EGO POWER+_files/out">
		</div>
	</head>

	<body style="" id="content">
		<div class="headerWrap">
			<div class="header">
				<div class="container content">
					<!-- <div class="sixteen columns header_border"> -->
					<div class="sixteen columns">
						<div class="four columns logo alpha">
							<a href="#" title="EGO POWER+">
								<img src="/Account _ EGO POWER+_files/logo.png" alt="EGO POWER+">
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		</div>
		</div>

		<div class="outer account main">
			<div class="container main content">

				<div class="sixteen columns clearfix collection_nav">
					<h1 class="collection_title"><span class="header-green">Customer</span> Reset Password</h1>
					<div class="line white"></div>
				</div>

				<!-- End of DoubleClick Floodlight Tag: Please do not remove -->

				<div class="clearfix" id="customer">

					<div id="recover-password">
						<div class="six columns">

							<h2>Reset Password</h2>

							<form id="resetPwdForm" method="post" action="/api/v1/resets" accept-charset="UTF-8" novalidate="novalidate"><input type="hidden" name="utf8" value="✓">
								<input type="hidden" id="token" name="token" value="${token}">
								<input type="hidden" id="id" name="id" value="${id}">
								<input type="hidden" id="type" name="type" value="${type}" />
								<div id="recover_email" class="clearfix large_form">
									<label for="password" class="large">New Password</label>
									<input type="password" size="30" name="password" id="password" class="large required">
								</div>
								<div id="recover_email" class="clearfix large_form">
									<label for="password" class="large">Confirmation Password</label>
									<input type="password" size="30" name="password_confirmation" id="password_confirmation" class="large required">
								</div>
								<p class="action_bottom">
									<input class="btn action_button" type="button" value="Submit" onclick="return resetPassword()">
								</p>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>

</html>