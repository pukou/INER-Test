package com.bsoft.mob.ienr.the_recording;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Mao Jiqing on 2016/10/15.
 */




public class ChatMessageBean implements Parcelable {
        private String consultId;
        private String content;
        private String groupTime;
        private String paths;
        private String personId;
        private String personName;
        private int personType;
        private int readTag;
        private String sessionId;
        private int sessionStatus;
        private String sessionTime;
        private int sessionType;
        private int width;
        private int height;
        private int sendState;
        private float voiceDuration;
        private int sendType=ChatConst.COMPLETED;
        private List<String>pathList;
        private String picPath;
        private String vedioPath;

        public String getVedioPath() {
                return vedioPath;
        }

        public void setVedioPath(String vedioPath) {
                this.vedioPath = vedioPath;
        }

        public String getPicPath() {
                return picPath;
        }

        public void setPicPath(String picPath) {
                this.picPath = picPath;
        }

        public int getSendType() {
                return sendType;
        }

        public void setSendType(int sendType) {
                this.sendType = sendType;
        }

        public List<String> getPathList() {
                return pathList;
        }

        public void setPathList(List<String> pathList) {
                this.pathList = pathList;
        }

        public float getVoiceDuration() {
                return voiceDuration;
        }

        public void setVoiceDuration(float voiceDuration) {
                this.voiceDuration = voiceDuration;
        }

        public int getSendState() {
                return sendState;
        }

        public void setSendState(int sendState) {
                this.sendState = sendState;
        }

        public int getWidth() {
                return width;
        }

        public void setWidth(int width) {
                this.width = width;
        }

        public int getHeight() {
                return height;
        }

        public void setHeight(int height) {
                this.height = height;
        }

        public String getConsultId() {
                return consultId;
        }

        public void setConsultId(String consultId) {
                this.consultId = consultId;
        }

        public String getContent() {
                return content;
        }

        public void setContent(String content) {
                this.content = content;
        }

        public String getGroupTime() {
                return groupTime;
        }

        public void setGroupTime(String groupTime) {
                this.groupTime = groupTime;
        }

        public String getPaths() {
                return paths;
        }

        public void setPaths(String paths) {
                this.paths = paths;
        }

        public String getPersonId() {
                return personId;
        }

        public void setPersonId(String personId) {
                this.personId = personId;
        }

        public String getPersonName() {
                return personName;
        }

        public void setPersonName(String personName) {
                this.personName = personName;
        }

        public int getPersonType() {
                return personType;
        }

        public void setPersonType(int personType) {
                this.personType = personType;
        }

        public int getReadTag() {
                return readTag;
        }

        public void setReadTag(int readTag) {
                this.readTag = readTag;
        }

        public String getSessionId() {
                return sessionId;
        }

        public void setSessionId(String sessionId) {
                this.sessionId = sessionId;
        }

        public int getSessionStatus() {
                return sessionStatus;
        }

        public void setSessionStatus(int sessionStatus) {
                this.sessionStatus = sessionStatus;
        }

        public String getSessionTime() {
                return sessionTime;
        }

        public void setSessionTime(String sessionTime) {
                this.sessionTime = sessionTime;
        }

        public int getSessionType() {
                return sessionType;
        }

        public void setSessionType(int sessionType) {
                this.sessionType = sessionType;
        }

        @Override
        public int describeContents() {
                return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.consultId);
                dest.writeString(this.content);
                dest.writeString(this.groupTime);
                dest.writeString(this.paths);
                dest.writeString(this.personId);
                dest.writeString(this.personName);
                dest.writeValue(this.personType);
                dest.writeValue(this.readTag);
                dest.writeString(this.sessionId);
                dest.writeValue(this.sessionStatus);
                dest.writeString(this.sessionTime);
                dest.writeValue(this.sessionType);
        }

        public ChatMessageBean() {
        }

        protected ChatMessageBean(Parcel in) {
                this.consultId = in.readString();
                this.content = in.readString();
                this.groupTime = in.readString();
                this.paths = in.readString();
                this.personId = in.readString();
                this.personName = in.readString();
                this.personType = (int) in.readValue(int.class.getClassLoader());
                this.readTag = (int) in.readValue(int.class.getClassLoader());
                this.sessionId = in.readString();
                this.sessionStatus = (int) in.readValue(int.class.getClassLoader());
                this.sessionTime = in.readString();
                this.sessionType = (int) in.readValue(int.class.getClassLoader());
        }

        public static final Creator<ChatMessageBean> CREATOR = new Creator<ChatMessageBean>() {
                @Override
                public ChatMessageBean createFromParcel(Parcel source) {
                        return new ChatMessageBean(source);
                }

                @Override
                public ChatMessageBean[] newArray(int size) {
                        return new ChatMessageBean[size];
                }
        };
}
