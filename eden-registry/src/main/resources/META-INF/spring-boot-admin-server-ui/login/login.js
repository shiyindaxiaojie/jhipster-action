/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

$(function () {
  initValidator(login);
  refreshCaptcha();

  $(document).keyup(function (event) {
    if (event.keyCode == 13) {
      $("#form-login").data("bootstrapValidator").validate();
    }
  });

  if (/\?error/.test(window.location.href)) {
    $('#div-info').show().addClass('alert-danger').find('span').empty().text('抱歉:(系统认证发生了问题。');
  } else if (/\?timeout/.test(window.location.href)) {
    $('#div-info').show().addClass('alert-warning').find('span').empty().text('系统未认证或者已失效！');
  } else if (/\?logout/.test(window.location.href)) {
    $('#div-info').show().addClass('alert-info').find('span').empty().text('您已成功退出！');
  }
  setTimeout(function () {
    $('#div-info').hide();
  }, 2000);
});

function initValidator(callback) {
  $('#form-login').bootstrapValidator({
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
      username: {
        message: '用户名无效',
        validators: {
          notEmpty: {
            message: '用户名不能为空'
          },
          stringLength: {
            min: 1,
            max: 18,
            message: '用户名必须大于1且少于30个字符'
          },
          regexp: {
            regexp: /^[a-zA-Z0-9_\.]+$/,
            message: '用户名只能包含字母，数字，小数点和下划线'
          }
        }
      },
      password: {
        validators: {
          notEmpty: {
            message: '密码不能为空'
          },
        }
      },
      captcha: {
        validators: {
          notEmpty: {
            message: '验证码不能为空'
          },
          callback: {
            message: '验证码错误',
            callback: function (value, validator) {
              var captcha = $.trim($('#captcha').val());
              var hiddenCaptcha = $('#captchaHidden').val();
              if (captcha === '') {
                return true;
              }
              return hiddenCaptcha.toLowerCase() === captcha.toLowerCase();
            }
          }
        }
      }
    }
  }).on('success.form.bv', function (e) {
    e.preventDefault();
    var $form = $(e.target);
    var bv = $form.data('bootstrapValidator');
    callback();
  });
}

function login() {
  var formObj = $('#form-login');
  var formParams = formObj.serializeArray();
  var formData = {};
  for (var item in formParams) {
    if (formParams[item].name === 'password') {
      formData[formParams[item].name] = md5(formParams[item].value);
    } else {
      formData[formParams[item].name] = formParams[item].value;
    }
  }
  jwtAuthentication(formData);
}

function jwtAuthentication(formData) {
  $.ajax({
    async: false,
    cache: false,
    type: 'POST',
    contentType: 'application/json;charset=utf-8',
    url: '/jwt/token',
    dataType: "json",
    data: JSON.stringify(formData),
    error: function (xhr, status, error) {
      authenticatedFailedHandle(xhr, status, error);
    },
    success: function (result) {
      // authenticatedSuccessHandle();
      if (result || result.hasOwnProperty('value')) {
        if (formData.rememberMe === 'true') {
          localStorage.setItem('JwtAuthorization', result.value);
        } else {
          sessionStorage.setItem('JwtAuthorization', result.value);
        }
        authenticatedSuccessHandle();
      }
    }
  });
}

function oauth2Authentication(formData) {
  $.ajax({
    async: false,
    cache: false,
    type: 'POST',
    contentType: 'application/json;charset=utf-8',
    url: '/auth/login',
    dataType: "json",
    data: JSON.stringify(formData),
    error: function (xhr, status, error) {
      authenticatedFailedHandle(xhr, status, error);
    },
    success: function (result) {
      if (result || result.hasOwnProperty('value')) {
        authenticatedSuccessHandle();
      }
    }
  });
}

function authenticatedSuccessHandle() {
  window.location.href = '/admin/main.html';
}

function authenticatedFailedHandle(xhr, status, error) {
  $('#div-info').show().addClass('alert-warning').find('span').empty().text('登录失败，原因：用户或密码无效！');
}

$('#captchaCanvas').click(function (e) {
  e.preventDefault();
  refreshCaptcha();
});

function refreshCaptcha() {
  var str = 'ABCEFGHJKLMNPQRSTWXY123456789';
  var text = '';
  for (var i = 0; i < 4; i++) {
    var txt = str[randomNum(0, str.length)];
    text += txt;
  }
  $('#captcha').val('');
  $('#captchaHidden').val(text);
  $('#captchaCanvas').text(text);
}

function drawCaptchaCanvas() {
  var canvas = document.getElementById("captchaCanvas");
  var width = canvas.width;
  var height = canvas.height;
  var ctx = canvas.getContext('2d');
  ctx.textBaseline = 'bottom';
  ctx.fillStyle = randomColor(180, 240);
  ctx.fillRect(0, 0, width, height);
  var str = 'ABCEFGHJKLMNPQRSTWXY123456789';
  var text = '';
  for (var i = 0; i < 4; i++) {
    var txt = str[randomNum(0, str.length)];
    ctx.fillStyle = randomColor(50, 160);
    ctx.font = randomNum(15, 40) + 'px SimHei';
    var x = 10 + i * 25;
    var y = randomNum(25, 45);
    var deg = randomNum(-45, 45);
    ctx.translate(x, y);
    ctx.rotate(deg * Math.PI / 180);
    ctx.fillText(txt, 0, 0);
    ctx.rotate(-deg * Math.PI / 180);
    ctx.translate(-x, -y);
    text += txt;
  }
  $('#captchaHidden').val(text);
  for (var i = 0; i < 8; i++) {
    ctx.strokeStyle = randomColor(40, 180);
    ctx.beginPath();
    ctx.moveTo(randomNum(0, width), randomNum(0, height));
    ctx.lineTo(randomNum(0, width), randomNum(0, height));
    ctx.stroke();
  }
  for (var i = 0; i < 100; i++) {
    ctx.fillStyle = randomColor(0, 255);
    ctx.beginPath();
    ctx.arc(randomNum(0, width), randomNum(0, height), 1, 0, 2 * Math.PI);
    ctx.fill();
  }
}

function randomNum(min, max) {
  return Math.floor(Math.random() * (max - min) + min);
}

function randomColor(min, max) {
  var r = randomNum(min, max);
  var g = randomNum(min, max);
  var b = randomNum(min, max);
  return "rgb(" + r + "," + g + "," + b + ")";
}
