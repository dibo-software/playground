# 升级文档
## v3.1.2 升级至 v3.2.0
CommonController中下列接口修改：
1. 批量加载选项数据通用接口调整：
```java
*@PostMapping("/load-related-data")* 替换为 *@PostMapping("/batch-load-related-data")*
```

2. 加载选项数据通用接口调整：
```java
@GetMapping({"/load-related-data", "/load-related-data/{parentId}"})
public JsonResult<List<LabelValue>> bindDataFilter(@Valid RelatedDataDTO relatedDataDTO, String keyword,
                                                   @PathVariable(required = false) String parentId) {
    return JsonResult.OK(loadRelatedData(relatedDataDTO, parentId, keyword));
}
```
修改为：
```java
@PostMapping({"/load-related-data", "/load-related-data/{parentId}"})
public JsonResult<List<LabelValue>> bindDataFilter(@Valid @RequestBody RelatedDataDTO relatedDataDTO,
                                                   @RequestParam(required = false) String keyword,
                                                   @PathVariable(required = false) String parentId) {
    return JsonResult.OK(loadRelatedData(relatedDataDTO, parentId, keyword));
}
```

## v3.1.0 升级至 v3.1.2

* 执行以下升级SQL（MySQL为例）
```sql
-- since v3.1.0
ALTER TABLE dbt_dictionary ADD COLUMN `app_module`  varchar(50) null comment '应用模块' AFTER tenant_id;
ALTER TABLE dbtlc_model_relation ADD COLUMN bridge_model_storage_key varchar(100) AFTER bridge_model_key;
ALTER TABLE dbtlc_model_relation CHANGE ext_condition extension varchar(200) NULL;
```