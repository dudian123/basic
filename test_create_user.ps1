$body = @{
    "username" = "admin"
    "password" = "admin123"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -Body $body -ContentType "application/json"

$responseContent = $response.Content | ConvertFrom-Json

$accessToken = $responseContent.data.access_token

Write-Host "AccessToken: $accessToken"

$headers = @{
    "Authorization" = "Bearer $accessToken"
}

$body = '{
    "userName": "testuser2",
    "nickName": "Test User 2",
    "password": "password123",
    "email": "testuser2@example.com",
    "phonenumber": "12345678902",
    "sex": "0",
    "status": "0",
    "roleIds": [2],
    "postIds": [2]
}'

Invoke-WebRequest -Uri "http://localhost:8080/system/user" -Method POST -Headers $headers -Body $body -ContentType "application/json"