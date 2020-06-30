package com.bsoft.mob.ienr.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.bsoft.mob.ienr.AppApplication;

/**
 * 数据库表辅助类
 */
public final class Database {
    // 对应 AndroidManifest.xml  <provider />
    //###public static final String AUTHORITY = "com.bsoft.mob.ienr.DBContentProvider";
    public static String AUTHORITY = AppApplication.getInstance().getPackageName() + ".DBContentProvider";

    // This class cannot be instantiated
    private Database() {

    }

    /**
     * 用户
     *
     * @ClassName: User
     * @Description: 保存当前用户
     */
    public static final class User implements BaseColumns {
        // This class cannot be instantiated
        private User() {
        }

     /*   public Uri getContentURI() {
            Uri fileUri;
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = dba.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
            } else {
                fileUri = Uri.fromFile(file);
            }
            return fileUri;
        }
*/
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/t_user");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of items.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.bsoft.mob.ienr.user";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * item.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.bsoft.mob.ienr.user";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id ASC";

        /**
         * 用户名
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String USER_NAME = "user_name";

        /**
         * 密码
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String PASSWORD = "password";

        /**
         * 员工ID
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String REMOTE_ID = "remote_id";

        /**
         * 员工所属机构ID
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String AGENT_ID = "agent_id";

    }

    /**
     * 消息主题
     *
     * @ClassName: Topic
     * @Description: 消息主题
     */
    public static final class Topic implements BaseColumns {
        // This class cannot be instantiated
        private Topic() {
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/t_topic");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of items.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.bsoft.mob.ienr.topic";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * item.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.bsoft.mob.ienr.topic";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id ASC";

        /**
         * 消息主题
         * <p/>
         * Type: INTEGER
         * </P>
         */
        public static final String TOPIC = "topic";

    }

    /**
     * 消息主体
     *
     * @ClassName: Message
     * @Description: 消息主题
     */
    public static final class Message implements BaseColumns {
        // This class cannot be instantiated
        private Message() {
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/t_message");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of items.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.bsoft.mob.ienr.message";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * item.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.bsoft.mob.ienr.message";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id ASC";

        /**
         * 消息主题
         * <p/>
         * Type: INTEGER
         * </P>
         */
        public static final String TOPIC = "topic";

        /**
         * 接收时间
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String RECEIVE_TIME = "receive_time";

        /**
         * 消息内容
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String CONTENT = "content";

        /**
         * 当前状态，0为未读，1为读取
         * <p/>
         * Type: INTEGER
         * </P>
         */
        public static final String STATE = "state";

        /**
         * 消息所属用户
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String USER = "user";

        /**
         * 机构ID
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String AGENT_ID = "agent_id";

        /**
         * 业务ID
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String BUSINESS_ID = "business_id";
        /**
         * 服务器ID
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String REMOTE_ID = "remote_id";
    }

    /**
     * 设置
     *
     * @ClassName: Setting
     * @Description: 保存设置内容
     */
    public static final class Setting implements BaseColumns {
        // This class cannot be instantiated
        private Setting() {
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/t_setting");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of items.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.bsoft.mob.ienr.components.setting";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * item.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.bsoft.mob.ienr.components.setting";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id ASC";

        /**
         * 消息震动提示,1震动，0静，默认为-1
         * <p/>
         * Type: INTEGER
         * </P>
         */
        public static final String VIB = "vib";

        /**
         * 设置所属用户
         * <p/>
         * Type: TEXT
         * </P>
         */
        public static final String USER = "user";

    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: OffLine
     * @Description: 离线保存
     * @date 2015-12-24 上午9:20:48
     */
    public static final class OffLine implements BaseColumns {
        // This class cannot be instantiated
        private OffLine() {
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/t_offline");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of items.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.bsoft.mob.ienr.offline";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * item.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.bsoft.mob.ienr.offline";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id ASC";

        //		URL, 请求类型(1(get),2(post)),参数(json字符串),病人姓名,记录名称,同步标志, 创建时间,创建护士,同步时间,同步人
        public static final String URL = "url";

        public static final String TYPE = "type";

        public static final String PARAM = "param";

        public static final String PATIENT = "patient";

        public static final String RECODENAME = "record_name";

        public static final String FLAG = "flag";

        public static final String CREATETIME = "create_time";

        public static final String CREATE_NURSE = "create_nurse";

        public static final String SYNCHRO_TIME = "synchro_time";

        public static final String SYNCHRO_NURSE = "synchro_nurse";
    }
}
