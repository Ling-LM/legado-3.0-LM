# Checklist

- [x] drawCurrentPageShadow() 第二段阴影在 mIsRtOrLb=false 时使用 BOTTOM_TOP 渐变方向
- [x] drawCurrentPageShadow() 第二段阴影在 mIsRtOrLb=true 时使用 TOP_BOTTOM 渐变方向
- [x] drawCurrentBackArea() 折叠阴影在 mIsRtOrLb=true 时使用 LEFT_RIGHT 渐变方向
- [x] drawCurrentBackArea() 折叠阴影在 mIsRtOrLb=false 时使用 RIGHT_LEFT 渐变方向
- [x] drawNextPageAreaAndShadow() 背面阴影在不同翻页方向下表现正确
- [x] 编译通过：./gradlew assembleAppDebug 成功（exit code 0）
- [x] 右上角翻页视觉效果保持不变或更优
- [x] 右下角翻页视觉效果与右上角一致