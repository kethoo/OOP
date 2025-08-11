<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome</title>
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
        form {
            margin: 15px 0;
        }
        input[type="text"], input[type="password"] {
            width: 200px;
            padding: 2px;
            margin: 2px 0;
        }
        input[type="submit"] {
            padding: 2px 8px;
            margin-left: 5px;
        }
        a {
            color: blue;
            text-decoration: underline;
        }
        .form-row {
            margin: 5px 0;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Welcome to Homework 5</h1>

    <p>Please log in:</p>

    <form action="LoginServlet" method="post">
        <div class="form-row">
            <label>User Name:</label><br>
            <label>
                <input type="text" name="username" required>
            </label>
        </div>
        <div class="form-row">
            <label>Password:</label>
            <input type="submit" value="Login">
        </div>
        <div class="form-row">
            <label>
                <input type="password" name="password" required>
            </label>
        </div>
    </form>

    <p><a href="create-account.jsp">Create New Account</a></p>
</div>
</body>
</html>