<#-- Настройки форматирования -->
<#setting date_format="yyyy-MM-dd">
<#setting time_format="HH:mm:ss">
<#setting time_zone="Europe/Paris">
<#setting datetime_format="yyyy-MM-dd HH:mm:ss">
<#setting locale="en_US">

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>You have tasks to do in 1 hour</title>
    <style>
        #body {
            display: flex;
            flex-direction: column;
            font-family: "Segoe UI", serif;
            align-items: center;
            background-color: #607FF2;
            font-size: 20px;
            height: 100%;
        }

        #div {
            display: block;
            flex-direction: column;
            margin: 10%;
            font-family: "Segoe UI", serif;
            background-color: white;
            padding: 3%;
            border-radius: 20px;
            color: black;
        }

        #button {
            background-color: #607FF2;
            border-radius: 10px;
            padding: 5px;
            color: white;
            text-decoration: none;
        }

        #a {
            text-decoration: none;
        }
    </style>
</head>
<body>
<div id="body">
    <div id="div">
        <h1 style="text-align: center">Reminder</h1>
        <p>Hello, ${firstname}!</p>
        <p>This is a friendly reminder that you have tasks to do in 1 hour.</p>
        <div>
            <#list tasks as task>
                <hr>
                <p>Title: ${task.name}</p>
                <#if task.description??>
                    <p>Description: ${task.description}</p>
                </#if>
                <p>Due: ${task.taskDueInfo.dueDateTime.format('yyyy-MM-dd HH:mm', task.taskDueInfo.timeZone)}</p>
                <hr>
            </#list>
        </div>
        <p>You can get more information about these tasks in the app.</p>
    </div>
</div>
</body>
</html>