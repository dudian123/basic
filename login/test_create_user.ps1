$body_login = @{
    username = 'admin'
    password = 'admin123'
} | ConvertTo-Json

$response_login = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method Post -Body $body_login -ContentType "application/json"
Write-Output $response_login.Content
$token = ($response_login.Content | ConvertFrom-Json).data.access_token
$access_token = "Bearer " + $token

$random_number = Get-Random -Minimum 1000 -Maximum 9999
$username = "testuser" + $random_number

$body_create_user = @{
    userName = $username
    nickName = $username
    password = "testpassword"
    email = $username + "@example.com"
    phonenumber = "13800138000"
    sex = "0"
    status = "0"
    roleIds = @(2)
    postIds = @(2)
    deptId = 103
} | ConvertTo-Json

$headers = @{
    "Authorization" = $access_token
    "Content-Type" = "application/json"
}

$response_create_user = Invoke-WebRequest -Uri "http://localhost:8080/system/user" -Method Post -Headers $headers -Body $body_create_user

Write-Output $response_create_user.Content