package org.example.config.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.AllArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.example.config.properties.TenantProperties;
import org.example.utils.TenantHelper;

import java.util.List;

/**
 * 自定义租户处理器
 *
 * @author ruoyi
 */
@AllArgsConstructor
public class PlusTenantLineHandler implements TenantLineHandler {

    private final TenantProperties tenantProperties;

    /**
     * 获取租户 ID 值表达式，只支持单个 ID 值
     *
     * @return 租户 ID 值表达式
     */
    @Override
    public Expression getTenantId() {
        // 避免循环调用，只从动态租户获取，不调用TenantHelper.getTenantId()
        String tenantId = TenantHelper.getDynamic();
        if (tenantId == null) {
            tenantId = "000000"; // 使用默认租户ID
        }
        return new StringValue(tenantId);
    }

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    /**
     * 根据表名判断是否忽略拼接多租户条件
     * <p>
     * 默认都要进行解析并拼接多租户条件
     *
     * @param tableName 表名
     * @return 是否忽略, true:表示忽略，false:需要解析并拼接多租户条件
     */
    @Override
    public boolean ignoreTable(String tableName) {
        // 避免循环调用，只从动态租户获取
        String tenantId = TenantHelper.getDynamic();
        // 判断是否有租户
        if (tenantId == null) {
            return true;
        }
        // 判断是否在忽略列表中
        List<String> excludeTables = tenantProperties.getExcludeTables();
        return excludeTables != null && excludeTables.contains(tableName);
    }

}