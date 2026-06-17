# 批量生成Service和Controller文件的PowerShell脚本

$entityPath = "F:\campus-medical-server\src\main\java\com\campus\medical\entity"
$servicePath = "F:\campus-medical-server\src\main\java\com\campus\medical\service"
$serviceImplPath = "$servicePath\impl"
$controllerPath = "F:\campus-medical-server\src\main\java\com\campus\medical\controller"
$mapperPath = "F:\campus-medical-server\src\main\java\com\campus\medical\mapper"

# 获取所有实体类文件
$entityFiles = Get-ChildItem -Path $entityPath -Filter "*.java"

foreach ($file in $entityFiles) {
    $entityName = $file.BaseName
    
    # 跳过已经创建的Service
    if ($entityName -eq "SysUser" -or $entityName -eq "MedicalDepartment" -or $entityName -eq "MedicalDoctor") {
        continue
    }
    
    # 计算包名（去掉Medical或Sys前缀用于路径）
    $serviceName = "${entityName}Service"
    $serviceImplName = "${entityName}ServiceImpl"
    $controllerName = "${entityName}Controller"
    $mapperName = "${entityName}Mapper"
    
    # 确定模块路径
    $modulePath = "/api/" + $entityName.ToLower()
    if ($entityName.StartsWith("Sys")) {
        $modulePath = "/api/system/" + $entityName.Substring(3).ToLower()
    } elseif ($entityName.StartsWith("Medical")) {
        $modulePath = "/api/medical/" + $entityName.Substring(7).ToLower()
    }
    
    # 生成Service接口
    $serviceContent = @"
package com.campus.medical.service;

import com.campus.medical.entity.$entityName;
import com.campus.medical.service.IBaseService;

/**
 * ${entityName}服务接口
 */
public interface ${serviceName} extends IBaseService<${entityName}> {
}
"@
    $serviceContent | Out-File -FilePath "$servicePath\$serviceName.java" -Encoding UTF8
    
    # 生成Service实现类
    $serviceImplContent = @"
package com.campus.medical.service.impl;

import com.campus.medical.entity.$entityName;
import com.campus.medical.mapper.${mapperName};
import com.campus.medical.service.${serviceName};
import org.springframework.stereotype.Service;

/**
 * ${entityName}服务实现类
 */
@Service
public class ${serviceImplName} extends BaseServiceImpl<${mapperName}, ${entityName}> implements ${serviceName} {
}
"@
    $serviceImplContent | Out-File -FilePath "$serviceImplPath\$serviceImplName.java" -Encoding UTF8
    
    # 生成Controller
    $controllerContent = @"
package com.campus.medical.controller;

import com.campus.medical.entity.$entityName;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.${serviceName};
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ${entityName}控制器
 */
@RestController
@RequestMapping("${modulePath}")
public class ${controllerName} extends BaseController<${entityName}> {

    @Autowired
    private ${serviceName} service;

    @Override
    protected IBaseService<${entityName}> getService() {
        return service;
    }
}
"@
    $controllerContent | Out-File -FilePath "$controllerPath\$controllerName.java" -Encoding UTF8
    
    Write-Host "已生成: $serviceName, $serviceImplName, $controllerName"
}

Write-Host "`n批量生成完成！"
