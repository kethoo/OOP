<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome <%= request.getAttribute("username") %></title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f0f0f0;
        }
        .container {
            background-color: white;
            padding: 20px;
            border: 1px solid #ccc;
            max-width: 400px;
        }
        h1 {
            font-size: 18px;
            margin-bottom: 15px;
        }
        a {
            color: blue;
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Welcome <%= request.getAttribute("username") %></h1>
</div>
</body>
</html>