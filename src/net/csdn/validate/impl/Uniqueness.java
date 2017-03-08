package net.csdn.validate.impl;

import net.csdn.common.collections.WowCollections;
import net.csdn.jpa.model.JPQL;
import net.csdn.validate.BaseValidateParse;
import net.csdn.validate.ValidateHelper;
import net.csdn.validate.ValidateResult;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.csdn.common.collections.WowCollections.map;

/**
 * BlogInfo: WilliamZhu
 * Date: 12-7-4
 * Time: 上午7:10
 */
public class Uniqueness extends BaseValidateParse {
    private static String notice = "{} is not uniq";

    @Override
    public void parse(final Object target, final List<ValidateResult> validateResultList) {
        try {
            final Class clzz = target.getClass();
            iterateValidateInfo(clzz, ValidateHelper.uniqueness, new ValidateIterator() {
                @Override
                public void iterate(String targetFieldName, Field field, Object info) throws Exception {
                    String msg = Uniqueness.notice;
                    if ((info instanceof Map))
                        msg = Uniqueness.this.messageWithDefault((Map) info, Uniqueness.notice);

                    Field tempField = clzz.getDeclaredField(targetFieldName);
                    tempField.setAccessible(true);

                    Object value = tempField.get(target);
                    Field idField = clzz.getDeclaredField("id");

                    idField.setAccessible(true);
                    Object id = idField.get(target);

                    List models = new ArrayList();

                    if (id != null) {
                        String whereCondition = targetFieldName + "=:hold and id<>:id";
                        models = ((JPQL)clzz.getDeclaredMethod("where", new Class[] { String.class, Map.class }).invoke(null, new Object[] { whereCondition, WowCollections.map(new Object[]{"hold", value, "id", id}) })).fetch();
                    } else {
                        String whereCondition = targetFieldName + "=:hold";
                        models = ((JPQL)clzz.getDeclaredMethod("where", new Class[] { String.class, Map.class }).invoke(null, new Object[] { whereCondition, WowCollections.map(new Object[]{"hold", value}) })).fetch();
                    }

                    if (models.size() > 0)
                        validateResultList.add(Uniqueness.this.validateResult(msg, targetFieldName));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
