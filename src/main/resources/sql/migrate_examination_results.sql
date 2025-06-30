-- 数据库迁移脚本：为examination_results表添加group_id字段
-- 执行前请备份数据库！

-- 1. 检查是否需要添加group_id字段
DO $$ 
BEGIN 
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'examination_results' 
        AND column_name = 'group_id'
    ) THEN
        -- 添加group_id字段
        ALTER TABLE examination_results 
        ADD COLUMN group_id INTEGER;
        
        -- 通过appointments表获取group_id并更新examination_results
        UPDATE examination_results 
        SET group_id = a.group_id 
        FROM appointments a 
        WHERE examination_results.appointment_id = a.appointment_id;
        
        -- 设置字段为非空并添加外键约束
        ALTER TABLE examination_results 
        ALTER COLUMN group_id SET NOT NULL;
        
        ALTER TABLE examination_results 
        ADD CONSTRAINT fk_examination_results_group 
        FOREIGN KEY (group_id) REFERENCES check_groups(group_id) ON DELETE CASCADE;
        
        -- 添加索引
        CREATE INDEX IF NOT EXISTS idx_examination_results_group ON examination_results(group_id);
        
        RAISE NOTICE 'examination_results表已成功添加group_id字段';
    ELSE
        RAISE NOTICE 'examination_results表已包含group_id字段，无需迁移';
    END IF;
END $$; 