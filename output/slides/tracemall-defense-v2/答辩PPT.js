const pptxgen = require("pptxgenjs");
const {
  imageSizingContain,
  imageSizingCrop,
} = require("./pptxgenjs_helpers/image");
const {
  warnIfSlideHasOverlaps,
  warnIfSlideElementsOutOfBounds,
} = require("./pptxgenjs_helpers/layout");
const path = require("path");

const pptx = new pptxgen();
pptx.layout = "LAYOUT_WIDE";
pptx.author = "OpenAI Codex";
pptx.company = "TraceMall";
pptx.subject = "可溯源的水果商城设计与实现答辩PPT";
pptx.title = "可溯源的水果商城设计与实现";
pptx.lang = "zh-CN";
pptx.theme = {
  headFontFace: "DengXian",
  bodyFontFace: "Microsoft YaHei",
  lang: "zh-CN",
};

const W = 13.333;
const H = 7.5;

const COLORS = {
  bg: "F5F7FA",
  panel: "FFFFFF",
  line: "D9DEE5",
  muted: "8A97A6",
  blue: "44556B",
  ink: "111111",
  softBlue: "E9EEF4",
  softDark: "EEF2F6",
  accent: "70869F",
  high: "C76464",
  green: "6C8A6B",
};

const ASSET = (...parts) => path.join(__dirname, "assets", ...parts);
const IMAGES = {
  merchant: ASSET("image11.png"),
  cart: ASSET("image14.png"),
  orders: ASSET("image15.png"),
  verify: ASSET("image18.png"),
  audit: ASSET("image20.png"),
};

const slideFrom = Number(process.env.SLIDE_FROM || 1);
const slideTo = Number(process.env.SLIDE_TO || 16);
const outputName =
  process.env.OUTPUT_NAME ||
  (slideFrom === 1 && slideTo === 16
    ? "答辩PPT.pptx"
    : `答辩PPT-${slideFrom}-${slideTo}.pptx`);

function includeSlide(page) {
  return page >= slideFrom && page <= slideTo;
}

function addBase(slide, section, page) {
  slide.background = { color: COLORS.bg };
  slide.addShape(pptx.ShapeType.rect, {
    x: 0.48,
    y: 0.42,
    w: 0.92,
    h: 0.1,
    line: { color: COLORS.blue, transparency: 100 },
    fill: { color: COLORS.blue },
  });
  slide.addText(section, {
    x: 0.52,
    y: 0.18,
    w: 2.1,
    h: 0.22,
    fontFace: "DengXian",
    fontSize: 10,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  slide.addText(String(page).padStart(2, "0"), {
    x: 12.52,
    y: 7.05,
    w: 0.42,
    h: 0.2,
    fontFace: "DengXian",
    fontSize: 10,
    color: COLORS.muted,
    align: "right",
    margin: 0,
  });
  slide.addText("TraceMall | 毕业答辩", {
    x: 0.52,
    y: 7.04,
    w: 2.6,
    h: 0.2,
    fontFace: "Microsoft YaHei",
    fontSize: 8,
    color: COLORS.muted,
    margin: 0,
  });
}

function addTitle(slide, title, subtitle) {
  slide.addText(title, {
    x: 0.9,
    y: 0.82,
    w: 5.6,
    h: 0.55,
    fontFace: "DengXian",
    fontSize: 24,
    bold: true,
    color: COLORS.ink,
    margin: 0,
  });
  if (subtitle) {
    slide.addText(subtitle, {
      x: 0.92,
      y: 1.42,
      w: 6.5,
      h: 0.34,
      fontFace: "Microsoft YaHei",
      fontSize: 10.5,
      color: COLORS.muted,
      margin: 0,
    });
  }
}

function addPanel(slide, x, y, w, h, opts = {}) {
  slide.addShape(pptx.ShapeType.roundRect, {
    x,
    y,
    w,
    h,
    rectRadius: opts.radius || 0.08,
    line: { color: opts.line || COLORS.line, width: opts.lineWidth || 0.8 },
    fill: { color: opts.fill || COLORS.panel, transparency: opts.transparency || 0 },
  });
}

function addBulletList(slide, items, x, y, w, fontSize = 12, color = COLORS.ink) {
  let top = y;
  items.forEach((item) => {
    slide.addShape(pptx.ShapeType.ellipse, {
      x,
      y: top + 0.065,
      w: 0.08,
      h: 0.08,
      line: { color },
      fill: { color },
    });
    slide.addText(item, {
      x: x + 0.16,
      y: top,
      w: w - 0.16,
      h: 0.3,
      fontFace: "Microsoft YaHei",
      fontSize,
      color,
      margin: 0,
    });
    top += 0.38;
  });
}

function addMetric(slide, x, y, w, h, label, value, note, fill) {
  addPanel(slide, x, y, w, h, { fill: fill || COLORS.softDark, line: fill || COLORS.softDark });
  slide.addText(label, {
    x: x + 0.18,
    y: y + 0.14,
    w: w - 0.36,
    h: 0.22,
    fontSize: 9.5,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  slide.addText(value, {
    x: x + 0.18,
    y: y + 0.42,
    w: w - 0.36,
    h: 0.34,
    fontSize: 18,
    color: COLORS.ink,
    bold: true,
    margin: 0,
  });
  slide.addText(note, {
    x: x + 0.18,
    y: y + h - 0.28,
    w: w - 0.36,
    h: 0.18,
    fontSize: 8.5,
    color: COLORS.muted,
    margin: 0,
  });
}

function addFlowBox(slide, x, y, w, h, title, body) {
  addPanel(slide, x, y, w, h, { fill: COLORS.panel });
  slide.addText(title, {
    x: x + 0.18,
    y: y + 0.12,
    w: w - 0.3,
    h: 0.22,
    fontSize: 10,
    bold: true,
    color: COLORS.blue,
    margin: 0,
  });
  slide.addText(body, {
    x: x + 0.18,
    y: y + 0.38,
    w: w - 0.3,
    h: h - 0.48,
    fontSize: 9.2,
    color: COLORS.ink,
    margin: 0.02,
    breakLine: false,
    valign: "mid",
  });
}

function addStepCard(slide, idx, x, y, w, h, title, body) {
  const compact = h < 1.0;
  const badgeY = y + 0.14;
  const titleY = y + (compact ? 0.42 : 0.5);
  const titleH = compact ? 0.2 : 0.28;
  const titleSize = compact ? 9.8 : 11;
  const bodyY = y + (compact ? 0.64 : 0.85);
  const bodyH = Math.max(h - (compact ? 0.72 : 0.94), compact ? 0.12 : 0.18);
  const bodySize = compact ? 7.8 : 8.8;
  addPanel(slide, x, y, w, h, { fill: COLORS.panel });
  slide.addShape(pptx.ShapeType.roundRect, {
    x: x + 0.16,
    y: badgeY,
    w: 0.46,
    h: 0.22,
    rectRadius: 0.06,
    line: { color: COLORS.softBlue, width: 0.6 },
    fill: { color: COLORS.softBlue },
  });
  slide.addText(`0${idx}`, {
    x: x + 0.16,
    y: y + 0.145,
    w: 0.46,
    h: 0.16,
    fontSize: 8.5,
    color: COLORS.blue,
    align: "center",
    bold: true,
    margin: 0,
  });
  slide.addText(title, {
    x: x + 0.16,
    y: titleY,
    w: w - 0.3,
    h: titleH,
    fontSize: titleSize,
    color: COLORS.ink,
    bold: true,
    margin: 0,
  });
  slide.addText(body, {
    x: x + 0.16,
    y: bodyY,
    w: w - 0.3,
    h: bodyH,
    fontSize: bodySize,
    color: COLORS.muted,
    margin: 0,
    valign: "top",
  });
}

function addScreenshot(slide, source, x, y, w, h, contain = false) {
  addPanel(slide, x, y, w, h, { fill: "F9FBFD", line: COLORS.line });
  const placement = contain
    ? imageSizingContain(source, x + 0.06, y + 0.06, w - 0.12, h - 0.12)
    : imageSizingCrop(source, x + 0.06, y + 0.06, w - 0.12, h - 0.12);
  slide.addImage({ path: source, ...placement });
}

function addImageLabel(slide, text, x, y, w) {
  slide.addText(text, {
    x,
    y,
    w,
    h: 0.18,
    fontSize: 8.5,
    color: COLORS.muted,
    align: "center",
    margin: 0,
  });
}

function finalize(slide) {
  warnIfSlideHasOverlaps(slide, pptx);
  warnIfSlideElementsOutOfBounds(slide, pptx);
}

// 1. Cover
if (includeSlide(1)) {
  const slide = pptx.addSlide();
  addBase(slide, "TraceMall Defense", 1);
  slide.addShape(pptx.ShapeType.rect, {
    x: 0,
    y: 0,
    w: W,
    h: H,
    line: { color: COLORS.bg, transparency: 100 },
    fill: { color: COLORS.bg },
  });
  slide.addShape(pptx.ShapeType.rect, {
    x: 0.9,
    y: 1.15,
    w: 0.12,
    h: 3.85,
    line: { color: COLORS.blue, transparency: 100 },
    fill: { color: COLORS.blue },
  });
  slide.addText("可溯源的水果商城\n设计与实现", {
    x: 1.28,
    y: 1.12,
    w: 5.3,
    h: 1.8,
    fontFace: "DengXian",
    fontSize: 28,
    bold: true,
    color: COLORS.ink,
    breakLine: false,
    margin: 0,
  });
  slide.addText("毕业论文答辩展示", {
    x: 1.32,
    y: 3.02,
    w: 3.5,
    h: 0.3,
    fontSize: 11,
    color: COLORS.blue,
    margin: 0,
  });
  addPanel(slide, 1.28, 3.5, 4.2, 1.55, { fill: COLORS.panel });
  slide.addText("珠海科技学院\n计算机学院 | 信息管理与信息系统", {
    x: 1.5,
    y: 3.82,
    w: 3.8,
    h: 0.62,
    fontSize: 14,
    color: COLORS.ink,
    bold: true,
    margin: 0,
  });
  slide.addText("答辩人：曾知行    学号：04222426\n指导教师：胡田圆讲师    日期：2026 年 3 月 4 日", {
    x: 1.5,
    y: 4.48,
    w: 3.8,
    h: 0.4,
    fontSize: 9.5,
    color: COLORS.muted,
    margin: 0,
  });
  slide.addShape(pptx.ShapeType.roundRect, {
    x: 8.55,
    y: 1.32,
    w: 3.65,
    h: 4.58,
    rectRadius: 0.12,
    line: { color: COLORS.line, width: 0.8 },
    fill: { color: COLORS.panel },
  });
  slide.addText("答辩主线", {
    x: 8.86,
    y: 1.66,
    w: 1.3,
    h: 0.24,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(slide, ["研究背景与问题提出", "系统分析与功能设计", "关键实现与工程机制", "测试验证与后续优化"], 8.9, 2.15, 2.85, 11.5, COLORS.ink);
  slide.addShape(pptx.ShapeType.line, {
    x: 8.88,
    y: 4.25,
    w: 2.88,
    h: 0,
    line: { color: COLORS.line, width: 0.8 },
  });
  slide.addText("关键词", {
    x: 8.86,
    y: 4.42,
    w: 1.0,
    h: 0.2,
    fontSize: 10,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  slide.addText("水果商城 / 批次溯源 / 二维码验真 /\n风险告警 / 审计管理", {
    x: 8.86,
    y: 4.72,
    w: 2.8,
    h: 0.6,
    fontSize: 11,
    color: COLORS.ink,
    margin: 0,
  });
  finalize(slide);
}

// 2. Background
if (includeSlide(2)) {
  const slide = pptx.addSlide();
  addBase(slide, "01 背景", 2);
  addTitle(slide, "研究背景与问题提出", "从传统水果电商的交易能力，延伸到可查、可验、可监管的可信闭环。");
  addMetric(slide, 0.95, 1.95, 2.7, 1.25, "传统痛点", "信息不透明", "难以准确了解来源、批次、质检与流转环节", COLORS.softBlue);
  addMetric(slide, 3.88, 1.95, 2.7, 1.25, "核心矛盾", "真伪难核验", "二维码常停留在展示层，缺乏篡改检测与可信校验", COLORS.softDark);
  addMetric(slide, 6.81, 1.95, 2.7, 1.25, "治理要求", "监管要可追责", "出现质量争议时，需要批次级追踪、告警与审计依据", COLORS.softBlue);
  addPanel(slide, 0.95, 3.55, 5.35, 2.48);
  slide.addText("问题提出", {
    x: 1.18,
    y: 3.82,
    w: 1.2,
    h: 0.22,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "普通商城能够完成浏览、下单、支付，但难以支撑批次级来源追踪。",
      "仅靠静态二维码无法有效判断是否被篡改，也难识别异常扫码行为。",
      "农产品流通场景更强调质量责任、问题召回与监管辅助，因此需要可信增强。",
      "毕业设计的目标不是重复 CRUD，而是验证交易链路与追溯链路能否协同落地。",
    ],
    1.18,
    4.18,
    4.8,
    11
  );
  addPanel(slide, 6.62, 3.55, 5.72, 2.48, { fill: COLORS.panel });
  slide.addText("论文与工程的切入点", {
    x: 6.88,
    y: 3.82,
    w: 1.8,
    h: 0.22,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addStepCard(slide, 1, 6.88, 4.22, 1.55, 1.34, "交易链路", "商品、购物车、订单、模拟支付，构成商城可演示闭环。");
  addStepCard(slide, 2, 8.63, 4.22, 1.55, 1.34, "溯源链路", "围绕水果批次记录采摘、质检、运输等事件。");
  addStepCard(slide, 3, 10.38, 4.22, 1.55, 1.34, "验真链路", "通过签名、锚点与异常扫描识别提升可信度。");
  finalize(slide);
}

// 3. Meaning and methods
if (includeSlide(3)) {
  const slide = pptx.addSlide();
  addBase(slide, "02 研究内容", 3);
  addTitle(slide, "研究意义、研究内容与方法", "新文档强调：本课题不仅要完成系统原型，更要形成可论证的设计与实现过程。");
  addPanel(slide, 0.95, 1.95, 3.72, 4.72);
  slide.addText("研究意义", {
    x: 1.18,
    y: 2.22,
    w: 1.1,
    h: 0.22,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "应用价值：提升水果商品信息透明度与消费者信任度。",
      "系统价值：把商城交易、批次溯源、验真防伪、监管辅助整合到同一平台。",
      "工程价值：为真实区块链接入、物流追踪和风控模型扩展预留接口。",
    ],
    1.18,
    2.6,
    3.08,
    10.8
  );
  addPanel(slide, 4.92, 1.95, 3.88, 4.72);
  slide.addText("研究内容", {
    x: 5.18,
    y: 2.22,
    w: 1.1,
    h: 0.22,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "分析三类角色的业务诉求与系统边界。",
      "确定 Vue 3 + Spring Boot 的前后端分离方案。",
      "设计商品、批次、事件、溯源码、订单、日志等核心数据对象。",
      "完成系统实现，并通过测试验证交易、追溯、验真与监管能力。",
    ],
    5.18,
    2.6,
    3.18,
    10.6
  );
  addPanel(slide, 9.05, 1.95, 3.28, 4.72);
  slide.addText("研究方法", {
    x: 9.3,
    y: 2.22,
    w: 1.1,
    h: 0.22,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addStepCard(slide, 1, 9.3, 2.72, 2.78, 0.82, "文献研究", "梳理农产品追溯、区块链与前后端分离实现相关研究。");
  addStepCard(slide, 2, 9.3, 3.72, 2.78, 0.82, "对比分析", "比较普通电商与可信追溯系统在能力边界上的差异。");
  addStepCard(slide, 3, 9.3, 4.72, 2.78, 0.82, "结构化设计", "围绕角色、流程和数据结构完成架构与数据库设计。");
  addStepCard(slide, 4, 9.3, 5.72, 2.78, 0.82, "原型实现与测试", "通过端到端实现验证方案可行性与工程落地性。");
  finalize(slide);
}

// 4. Goals and innovation
if (includeSlide(4)) {
  const slide = pptx.addSlide();
  addBase(slide, "03 定位", 4);
  addTitle(slide, "系统目标与创新点", "项目定位不是“普通水果商城”，而是“交易 + 溯源 + 验真 + 监管”的可信闭环系统。");
  addPanel(slide, 0.95, 1.95, 5.3, 4.68);
  slide.addText("系统目标", {
    x: 1.18,
    y: 2.22,
    w: 1,
    h: 0.2,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "实现商品展示、购物车、订单创建与模拟支付，满足基础交易能力。",
      "实现水果录入、批次创建与批次事件维护，使溯源数据围绕批次组织。",
      "实现 traceId 查询与二维码验真，让用户既能“查到”，也能“验证”。",
      "实现风险告警与审计日志查看，为监管辅助和责任定位提供依据。",
    ],
    1.18,
    2.58,
    4.7,
    11
  );
  addPanel(slide, 6.52, 1.95, 5.82, 4.68);
  slide.addText("创新点与特色能力", {
    x: 6.78,
    y: 2.22,
    w: 1.75,
    h: 0.2,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addStepCard(slide, 1, 6.82, 2.68, 2.46, 1.25, "四链闭环", "把交易链路、溯源链路、验真链路、监管链路统一到一个系统中。");
  addStepCard(slide, 2, 9.52, 2.68, 2.46, 1.25, "多规则验真", "不只返回真/假，而是输出 PASS、SUSPECT、HIGH_RISK、FAIL。");
  addStepCard(slide, 3, 6.82, 4.22, 2.46, 1.25, "可信增强", "以签名校验、锚点校验和异常扫描识别增强来源信息可信度。");
  addStepCard(slide, 4, 9.52, 4.22, 2.46, 1.25, "监管支撑", "通过告警落库与审计留痕，为平台治理和追责提供辅助能力。");
  finalize(slide);
}

// 5. Role needs
if (includeSlide(5)) {
  const slide = pptx.addSlide();
  addBase(slide, "04 需求", 5);
  addTitle(slide, "多角色需求分析", "系统围绕消费者、商家和监管人员三类角色设计功能入口与职责边界。");
  addPanel(slide, 0.95, 2.0, 3.76, 4.6);
  addPanel(slide, 4.79, 2.0, 3.76, 4.6);
  addPanel(slide, 8.63, 2.0, 3.76, 4.6);
  slide.addText("消费者", {
    x: 1.22,
    y: 2.28,
    w: 1.0,
    h: 0.22,
    fontSize: 16,
    bold: true,
    color: COLORS.ink,
    margin: 0,
  });
  addBulletList(slide, ["浏览商品与详情", "购物车管理与下单", "订单支付与状态查看", "按 traceId 查询批次摘要与事件时间线", "提交签名信息执行二维码验真"], 1.22, 2.72, 3.05, 10.5);
  slide.addText("商家", {
    x: 5.06,
    y: 2.28,
    w: 1.0,
    h: 0.22,
    fontSize: 16,
    bold: true,
    color: COLORS.ink,
    margin: 0,
  });
  addBulletList(slide, ["维护水果基础信息", "创建批次并生成 traceId / 签名", "补充采摘、质检、运输等事件", "查看本店商品与批次统计", "保证仅能访问本店数据"], 5.06, 2.72, 3.05, 10.5);
  slide.addText("监管人员", {
    x: 8.9,
    y: 2.28,
    w: 1.35,
    h: 0.22,
    fontSize: 16,
    bold: true,
    color: COLORS.ink,
    margin: 0,
  });
  addBulletList(slide, ["分页查看风险告警", "分页查看审计日志", "识别异常扫码与可疑验真", "辅助定位违规调用与责任主体", "形成平台治理与监管视角"], 8.9, 2.72, 3.0, 10.5);
  finalize(slide);
}

// 6. Functional structure
if (includeSlide(6)) {
  const slide = pptx.addSlide();
  addBase(slide, "05 设计", 6);
  addTitle(slide, "总体功能结构", "新文档明确将系统划分为消费者端、商家端、监管端、基础支撑四个部分。");
  addPanel(slide, 0.95, 1.95, 11.4, 4.72);
  const cols = [
    {
      x: 1.2,
      title: "消费者端",
      items: ["商品浏览", "商品详情", "购物车管理", "订单管理", "溯源查询", "二维码验真"],
    },
    {
      x: 4.0,
      title: "商家端",
      items: ["水果信息管理", "批次创建", "批次事件上报", "经营统计展示"],
    },
    {
      x: 6.8,
      title: "监管端",
      items: ["风险告警查看", "审计日志查看", "异常辅助分析"],
    },
    {
      x: 9.6,
      title: "基础支撑",
      items: ["用户认证", "角色权限控制", "统一响应处理", "异常处理", "审计记录", "TraceId 链路追踪"],
    },
  ];
  cols.forEach((col) => {
    slide.addShape(pptx.ShapeType.rect, {
      x: col.x,
      y: 2.5,
      w: 2.05,
      h: 0.42,
      line: { color: COLORS.blue, width: 1.0 },
      fill: { color: COLORS.softBlue },
    });
    slide.addText(col.title, {
      x: col.x,
      y: 2.59,
      w: 2.05,
      h: 0.18,
      fontSize: 10.5,
      bold: true,
      color: COLORS.ink,
      align: "center",
      margin: 0,
    });
    let top = 3.16;
    col.items.forEach((item) => {
      slide.addShape(pptx.ShapeType.roundRect, {
        x: col.x,
        y: top,
        w: 2.05,
        h: 0.42,
        rectRadius: 0.05,
        line: { color: COLORS.line, width: 0.8 },
        fill: { color: COLORS.panel },
      });
      slide.addText(item, {
        x: col.x + 0.08,
        y: top + 0.1,
        w: 1.9,
        h: 0.18,
        fontSize: 9,
        color: COLORS.ink,
        align: "center",
        margin: 0,
      });
      top += 0.56;
    });
  });
  finalize(slide);
}

// 7. Architecture
if (includeSlide(7)) {
  const slide = pptx.addSlide();
  addBase(slide, "06 架构", 7);
  addTitle(slide, "系统架构与四链闭环", "仓库实现采用前后端分离架构，并以链下明细 + 链上锚定的思路增强可信性。");
  addPanel(slide, 0.95, 1.98, 7.1, 4.6);
  slide.addText("分层架构", {
    x: 1.18,
    y: 2.24,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  const layers = [
    ["展示层", "Vue 3 / Vite / Pinia / Vue Router / ECharts"],
    ["业务层", "商品、订单、溯源、验真、监管查询"],
    ["服务层", "Spring Boot 3.5.10 / Spring Security / MyBatis / JWT / Flyway"],
    ["数据层", "MySQL 8 / Redis 7 / 审计日志 / 风险告警 / 批次与事件"],
    ["可信层", "事件哈希、Mock 链锚定、锚点校验接口预留"],
  ];
  layers.forEach((layer, idx) => {
    const y = 2.72 + idx * 0.68;
    slide.addShape(pptx.ShapeType.roundRect, {
      x: 1.18,
      y,
      w: 5.95,
      h: 0.5,
      rectRadius: 0.06,
      line: { color: COLORS.line, width: 0.8 },
      fill: { color: idx % 2 === 0 ? COLORS.panel : COLORS.softDark },
    });
    slide.addText(layer[0], {
      x: 1.36,
      y: y + 0.14,
      w: 0.92,
      h: 0.16,
      fontSize: 10,
      bold: true,
      color: COLORS.blue,
      margin: 0,
    });
    slide.addText(layer[1], {
      x: 2.34,
      y: y + 0.14,
      w: 4.45,
      h: 0.16,
      fontSize: 9.5,
      color: COLORS.ink,
      margin: 0,
    });
  });
  addPanel(slide, 8.28, 1.98, 4.07, 4.6);
  slide.addText("四链闭环", {
    x: 8.54,
    y: 2.24,
    w: 1.1,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  const chain = [
    { x: 8.7, y: 2.9, w: 1.28, h: 0.62, title: "交易链路", note: "商品 -> 购物车 -> 订单 -> 支付" },
    { x: 10.42, y: 2.9, w: 1.28, h: 0.62, title: "溯源链路", note: "水果 -> 批次 -> 事件 -> 时间线" },
    { x: 10.42, y: 4.05, w: 1.28, h: 0.62, title: "验真链路", note: "格式 -> 签名 -> 锚点 -> 风控" },
    { x: 8.7, y: 4.05, w: 1.28, h: 0.62, title: "监管链路", note: "告警 -> 审计 -> 追责辅助" },
  ];
  chain.forEach((item) => {
    addPanel(slide, item.x, item.y, item.w, item.h, { fill: COLORS.panel });
    slide.addText(item.title, {
      x: item.x + 0.08,
      y: item.y + 0.12,
      w: item.w - 0.16,
      h: 0.18,
      fontSize: 9.4,
      bold: true,
      align: "center",
      color: COLORS.ink,
      margin: 0,
    });
    slide.addText(item.note, {
      x: item.x + 0.08,
      y: item.y + 0.34,
      w: item.w - 0.16,
      h: 0.18,
      fontSize: 7.6,
      align: "center",
      color: COLORS.muted,
      margin: 0,
    });
  });
  slide.addShape(pptx.ShapeType.line, {
    x: 10.0,
    y: 3.22,
    w: 0.32,
    h: 0,
    line: { color: COLORS.blue, width: 1.0, beginArrowType: "none", endArrowType: "triangle" },
  });
  slide.addShape(pptx.ShapeType.line, {
    x: 11.02,
    y: 3.56,
    w: 0,
    h: 0.34,
    line: { color: COLORS.blue, width: 1.0, beginArrowType: "none", endArrowType: "triangle" },
  });
  slide.addShape(pptx.ShapeType.line, {
    x: 10.0,
    y: 4.36,
    w: 0.32,
    h: 0,
    line: { color: COLORS.blue, width: 1.0, beginArrowType: "triangle", endArrowType: "none" },
  });
  slide.addShape(pptx.ShapeType.line, {
    x: 8.98,
    y: 3.56,
    w: 0,
    h: 0.34,
    line: { color: COLORS.blue, width: 1.0, beginArrowType: "triangle", endArrowType: "none" },
  });
  finalize(slide);
}

// 8. Data flow and entities
if (includeSlide(8)) {
  const slide = pptx.addSlide();
  addBase(slide, "07 数据", 8);
  addTitle(slide, "数据流与核心实体", "新文档把数据流分为交易链路、溯源链路和监管链路，并给出了完整 ER 设计。");
  addPanel(slide, 0.95, 1.98, 6.05, 4.62);
  slide.addText("业务数据流", {
    x: 1.18,
    y: 2.24,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  const flowSteps = [
    ["消费者浏览与下单", "水果表、批次摘要表支撑商品展示；订单表与订单项表承载交易结果。"],
    ["商家创建批次", "批次表、库存表、溯源码表同时写入，形成 traceId 与签名绑定。"],
    ["事件补录与查询", "批次事件表按时间组织时间线，供 trace 查询结果聚合展示。"],
    ["验真与监管联动", "扫码日志、风险告警、审计日志共同支撑异常识别与追责辅助。"],
  ];
  flowSteps.forEach((s, idx) => {
    addStepCard(slide, idx + 1, 1.18, 2.72 + idx * 0.96, 5.55, 0.82, s[0], s[1]);
  });
  addPanel(slide, 7.22, 1.98, 5.13, 4.62);
  slide.addText("核心实体分组", {
    x: 7.48,
    y: 2.24,
    w: 1.35,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addFlowBox(slide, 7.48, 2.72, 1.36, 1.02, "身份权限", "tm_user\ntm_role\ntm_user_role");
  addFlowBox(slide, 9.01, 2.72, 1.36, 1.02, "店铺商品", "tm_shop\ntm_fruit");
  addFlowBox(slide, 10.54, 2.72, 1.36, 1.02, "批次追溯", "tm_batch\ntm_batch_event\ntm_trace_code");
  addFlowBox(slide, 7.48, 4.12, 1.36, 1.02, "交易数据", "tm_order\ntm_order_item\ntm_inventory");
  addFlowBox(slide, 9.01, 4.12, 1.36, 1.02, "监管数据", "tm_trace_scan_log\ntm_risk_alert");
  addFlowBox(slide, 10.54, 4.12, 1.36, 1.02, "可信与审计", "tm_chain_anchor\ntm_audit_log");
  slide.addText("逻辑结构遵循第三范式：实体独立、外键清晰、减少冗余，保证交易与追溯数据的一致性。", {
    x: 7.48,
    y: 5.55,
    w: 4.2,
    h: 0.38,
    fontSize: 9.2,
    color: COLORS.muted,
    margin: 0,
  });
  finalize(slide);
}

// 9. Batch creation
if (includeSlide(9)) {
  const slide = pptx.addSlide();
  addBase(slide, "08 实现", 9);
  addTitle(slide, "批次创建与溯源码生成", "实现目标：在批次创建阶段同时完成批次、库存与溯源码绑定，形成后续溯源与验真的统一入口。");
  addPanel(slide, 0.95, 1.92, 7.1, 4.7);
  slide.addText("后端关键逻辑", {
    x: 1.18,
    y: 2.18,
    w: 1.15,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addStepCard(slide, 1, 1.18, 2.62, 1.55, 1.2, "归属校验", "校验 fruit 是否属于当前商家店铺，阻止跨商家越权。");
  addStepCard(slide, 2, 2.93, 2.62, 1.55, 1.2, "traceId 生成", "支持前端传入或系统自动生成追溯编号。");
  addStepCard(slide, 3, 4.68, 2.62, 1.55, 1.2, "事务写入", "统一写入批次表、库存表与溯源码表，失败则整体回滚。");
  addStepCard(slide, 4, 1.18, 4.08, 2.46, 1.12, "签名计算", "以 signKey + traceId + batchNo 生成 signature，增强防篡改能力。");
  addStepCard(slide, 5, 3.86, 4.08, 2.46, 1.12, "结果返回", "返回 batchId、batchNo、traceId、signature，供前端展示与后续下载二维码。");
  addPanel(slide, 8.3, 1.92, 4.05, 4.7);
  slide.addText("前端表现", {
    x: 8.56,
    y: 2.18,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addScreenshot(slide, IMAGES.merchant, 8.52, 2.52, 3.62, 2.78, false);
  addImageLabel(slide, "商家端批次创建区域", 8.72, 5.38, 3.2);
  slide.addText("工程价值：把批次信息、库存状态、traceId 与签名在入口阶段统一绑定，后续查询与验真无需再做二次拼装。", {
    x: 8.56,
    y: 5.74,
    w: 3.35,
    h: 0.55,
    fontSize: 9.2,
    color: COLORS.muted,
    margin: 0,
  });
  finalize(slide);
}

// 10. Order implementation
if (includeSlide(10)) {
  const slide = pptx.addSlide();
  addBase(slide, "08 实现", 10);
  addTitle(slide, "订单交易实现", "实现目标：保证“提交订单 -> 扣减库存 -> 回写金额 -> 模拟支付”的交易闭环完整且一致。");
  addPanel(slide, 0.95, 1.92, 5.45, 4.72);
  slide.addText("核心流程", {
    x: 1.18,
    y: 2.18,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addStepCard(slide, 1, 1.18, 2.62, 1.98, 1.1, "下单请求", "从购物车收集批次与数量，创建订单主表。");
  addStepCard(slide, 2, 3.38, 2.62, 1.98, 1.1, "库存校验", "逐项校验 remainingQuantity 是否充足。");
  addStepCard(slide, 3, 1.18, 4.02, 1.98, 1.1, "事务扣减", "同步扣减 inventory.stock_qty 与 batch.remaining_quantity。");
  addStepCard(slide, 4, 3.38, 4.02, 1.98, 1.1, "模拟支付", "仅在待支付状态下允许支付，更新 paymentRef 与订单状态。");
  slide.addText("工程要点：OrderService 采用事务统一处理订单主表、订单项、库存与金额回写，避免部分成功导致的数据不一致。", {
    x: 1.18,
    y: 5.54,
    w: 4.9,
    h: 0.45,
    fontSize: 9.2,
    color: COLORS.muted,
    margin: 0,
  });
  addPanel(slide, 6.62, 1.92, 5.73, 4.72);
  slide.addText("前端表现", {
    x: 6.88,
    y: 2.18,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addScreenshot(slide, IMAGES.cart, 6.88, 2.52, 2.45, 2.78, false);
  addScreenshot(slide, IMAGES.orders, 9.55, 2.52, 2.45, 2.78, false);
  addImageLabel(slide, "购物车界面", 7.2, 5.38, 1.8);
  addImageLabel(slide, "订单与模拟支付界面", 9.72, 5.38, 2.1);
  finalize(slide);
}

// 11. Trace and verify
if (includeSlide(11)) {
  const slide = pptx.addSlide();
  addBase(slide, "08 实现", 11);
  addTitle(slide, "溯源查询与二维码验真", "实现目标：让用户既能按 traceId 查询批次时间线，也能通过多规则校验判断结果可信度。");
  addPanel(slide, 0.95, 1.92, 7.28, 4.74);
  slide.addText("验真逻辑", {
    x: 1.18,
    y: 2.18,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addStepCard(slide, 1, 1.18, 2.62, 1.55, 1.14, "格式校验", "校验 traceId 是否满足 TRACE-[A-Z0-9-]{4,64}。");
  addStepCard(slide, 2, 2.93, 2.62, 1.55, 1.14, "签名校验", "比较请求签名、数据库签名与重算签名是否一致。");
  addStepCard(slide, 3, 4.68, 2.62, 1.55, 1.14, "锚点校验", "读取关键质量事件，执行 anchorId 与 eventHash 一致性检查。");
  addStepCard(slide, 4, 1.18, 4.02, 2.46, 1.08, "异常扫描识别", "统计近期扫码次数、地理离散度与设备指纹，识别高频或异地扫描。");
  addPanel(slide, 3.88, 4.02, 2.35, 1.08, { fill: COLORS.softDark, line: COLORS.line });
  slide.addText("结果分级", {
    x: 4.06,
    y: 4.18,
    w: 0.9,
    h: 0.18,
    fontSize: 9.5,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  slide.addText("PASS / SUSPECT /\nHIGH_RISK / FAIL", {
    x: 4.06,
    y: 4.48,
    w: 1.65,
    h: 0.38,
    fontSize: 12,
    color: COLORS.ink,
    bold: true,
    margin: 0,
  });
  slide.addText("TraceService 并非只返回真假结论，而是返回状态、原因、风险等级和锚点匹配结果，使结果更可解释。", {
    x: 1.18,
    y: 5.54,
    w: 5.8,
    h: 0.42,
    fontSize: 9.2,
    color: COLORS.muted,
    margin: 0,
  });
  addPanel(slide, 8.48, 1.92, 3.87, 4.74);
  slide.addText("前端表现", {
    x: 8.74,
    y: 2.18,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addScreenshot(slide, IMAGES.verify, 8.68, 2.52, 3.46, 2.86, false);
  addImageLabel(slide, "验真结果界面（签名不一致 -> FAIL）", 8.9, 5.42, 3.0);
  slide.addText("界面同时展示状态、原因、风险等级与最近验真记录，完整呈现从“可查”到“可验”的业务链路。", {
    x: 8.74,
    y: 5.72,
    w: 3.1,
    h: 0.42,
    fontSize: 9.2,
    color: COLORS.muted,
    margin: 0,
  });
  finalize(slide);
}

// 12. Risk and audit
if (includeSlide(12)) {
  const slide = pptx.addSlide();
  addBase(slide, "08 实现", 12);
  addTitle(slide, "风险告警与审计管理", "实现目标：不仅发现异常，还要对异常进行结构化记录、留痕与监管查询。");
  addPanel(slide, 0.95, 1.92, 5.35, 4.74);
  slide.addText("告警生成逻辑", {
    x: 1.18,
    y: 2.18,
    w: 1.2,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "当验真模块发现格式非法、未知 traceId、签名不一致、锚点不一致或短时多次异地扫码时，生成风险告警记录。",
      "告警表保存告警类型、风险等级、状态、描述和关联批次，供监管端分页查看。",
      "风险识别的核心价值在于把异常结果从即时返回转化为后续可追踪的数据对象。",
    ],
    1.18,
    2.6,
    4.7,
    10.2
  );
  slide.addText("审计留痕逻辑", {
    x: 1.18,
    y: 4.45,
    w: 1.2,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "AuditLogInterceptor 统一拦截关键非 GET 请求。",
      "记录用户、模块、动作、请求路径、方法、状态码与来源 IP。",
      "当出现越权访问、异常调用或业务争议时，可用于过程还原与责任定位。",
    ],
    1.18,
    4.86,
    4.7,
    10.2
  );
  addPanel(slide, 6.55, 1.92, 5.8, 4.74);
  slide.addText("监管端界面", {
    x: 6.82,
    y: 2.18,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addScreenshot(slide, IMAGES.audit, 6.82, 2.52, 5.28, 3.36, false);
  addImageLabel(slide, "审计日志列表（真实系统界面）", 8.02, 5.96, 2.8);
  finalize(slide);
}

// 13. UI showcase
if (includeSlide(13)) {
  const slide = pptx.addSlide();
  addBase(slide, "09 展示", 13);
  addTitle(slide, "系统界面展示", "按角色展示商家、消费者、验真与监管四类真实页面，证明系统原型已形成可演示闭环。");
  addScreenshot(slide, IMAGES.merchant, 0.95, 2.02, 2.95, 2.1, false);
  addScreenshot(slide, IMAGES.cart, 4.12, 2.02, 2.95, 2.1, false);
  addScreenshot(slide, IMAGES.verify, 7.29, 2.02, 2.95, 2.1, false);
  addScreenshot(slide, IMAGES.audit, 10.46, 2.02, 1.89, 2.1, false);
  addImageLabel(slide, "商家端：批次创建", 1.55, 4.22, 1.75);
  addImageLabel(slide, "消费者端：购物车", 4.78, 4.22, 1.6);
  addImageLabel(slide, "消费者端：二维码验真", 7.78, 4.22, 1.95);
  addImageLabel(slide, "监管端：审计日志", 10.65, 4.22, 1.5);
  slide.addText("界面观察总结", {
    x: 1.18,
    y: 4.98,
    w: 1.2,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "消费者侧强调商品发现、下单支付与验真反馈的一体化体验。",
      "商家侧把“商品 + 批次 + 事件”集中在同一工作台，提高批次信息维护效率。",
      "监管侧通过风险记录与审计留痕形成平台治理视角，增强系统的可信与可追责属性。",
    ],
    1.18,
    5.38,
    10.6,
    10.5
  );
  slide.addShape(pptx.ShapeType.line, {
    x: 1.18,
    y: 4.82,
    w: 11.0,
    h: 0,
    line: { color: COLORS.line, width: 0.8 },
  });
  finalize(slide);
}

// 14. Test and deployment
if (includeSlide(14)) {
  const slide = pptx.addSlide();
  addBase(slide, "10 验证", 14);
  addTitle(slide, "测试与部署", "新文档提供了黑盒测试表；仓库侧已验证后端测试可运行、前端构建成功。");
  addPanel(slide, 0.95, 1.92, 7.12, 4.74);
  slide.addText("黑盒测试摘要", {
    x: 1.18,
    y: 2.18,
    w: 1.1,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  const rows = [
    ["商品浏览", "返回在售水果列表", "通过"],
    ["创建批次", "返回 batchId / traceId / signature", "通过"],
    ["提交订单", "订单生成且库存同步减少", "通过"],
    ["模拟支付", "状态由 PENDING_PAYMENT -> PAID", "通过"],
    ["溯源查询", "返回批次摘要与事件时间线", "通过"],
    ["验真失败", "错误签名返回 FAIL", "通过"],
    ["风险识别", "短时异地扫码 -> HIGH_RISK", "通过"],
    ["权限控制", "消费者访问商家接口被拒绝", "通过"],
  ];
  slide.addShape(pptx.ShapeType.rect, {
    x: 1.18,
    y: 2.6,
    w: 5.9,
    h: 0.36,
    line: { color: COLORS.line, width: 0.8 },
    fill: { color: COLORS.softBlue },
  });
  ["测试功能", "预期/实际结果", "结论"].forEach((htext, idx) => {
    slide.addText(htext, {
      x: [1.32, 2.8, 6.2][idx],
      y: 2.71,
      w: [1.2, 3.1, 0.5][idx],
      h: 0.14,
      fontSize: 8.8,
      bold: true,
      color: COLORS.ink,
      margin: 0,
    });
  });
  rows.forEach((row, idx) => {
    const y = 2.96 + idx * 0.39;
    slide.addShape(pptx.ShapeType.line, {
      x: 1.18,
      y,
      w: 5.9,
      h: 0,
      line: { color: COLORS.line, width: 0.6 },
    });
    slide.addText(row[0], { x: 1.32, y: y + 0.08, w: 1.3, h: 0.14, fontSize: 8.4, color: COLORS.ink, margin: 0 });
    slide.addText(row[1], { x: 2.8, y: y + 0.08, w: 3.0, h: 0.14, fontSize: 8.4, color: COLORS.ink, margin: 0 });
    slide.addText(row[2], { x: 6.2, y: y + 0.08, w: 0.5, h: 0.14, fontSize: 8.4, color: COLORS.green, bold: true, margin: 0 });
  });
  addPanel(slide, 8.3, 1.92, 4.05, 4.74);
  slide.addText("部署与工程事实", {
    x: 8.56,
    y: 2.18,
    w: 1.4,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "后端环境：JDK 17、Spring Boot 3.5.10、MyBatis 3.0.5、MySQL 8、Redis 7。",
      "前端环境：Node.js 18+、Vue 3.5.27、Vite 7.3.1，开发端口 5173。",
      "数据库通过 Flyway 自动初始化，演示数据可支持全链路答辩展示。",
      "已核验事实：`mvn test` 可运行，`npm run build` 成功。",
      "工程优化点：商家端打包 chunk 偏大，但不影响答辩演示。",
    ],
    8.56,
    2.6,
    3.32,
    9.7
  );
  finalize(slide);
}

// 15. Limitations
if (includeSlide(15)) {
  const slide = pptx.addSlide();
  addBase(slide, "11 展望", 15);
  addTitle(slide, "不足与优化方向", "系统已经形成完整原型，但在可信增强深度和业务闭环完整性上仍有扩展空间。");
  addPanel(slide, 0.95, 2.0, 5.5, 4.62);
  slide.addText("当前不足", {
    x: 1.18,
    y: 2.26,
    w: 1.0,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addBulletList(
    slide,
    [
      "链锚点校验当前仍以本地 Mock 和预留接口为主，未接入真实区块链平台。",
      "支付流程采用模拟支付，尚未接入真实第三方支付能力。",
      "风险识别主要依赖预设规则，监管分析仍以基础列表展示为主。",
      "物流、履约和更复杂的行为画像能力尚未纳入当前版本。",
    ],
    1.18,
    2.64,
    4.9,
    10.7
  );
  addPanel(slide, 6.78, 2.0, 5.57, 4.62);
  slide.addText("后续优化方向", {
    x: 7.02,
    y: 2.26,
    w: 1.35,
    h: 0.18,
    fontSize: 11,
    color: COLORS.blue,
    bold: true,
    margin: 0,
  });
  addStepCard(slide, 1, 7.02, 2.72, 2.36, 1.12, "真实可信存证", "接入 Fabric / EVM 或可信存证平台，提升事件防篡改能力。");
  addStepCard(slide, 2, 9.7, 2.72, 2.36, 1.12, "扩展业务闭环", "完善支付、物流、履约等环节，使商城能力更加完整。");
  addStepCard(slide, 3, 7.02, 4.18, 2.36, 1.12, "优化风控模型", "结合用户行为、设备指纹和历史扫码模式提升识别精度。");
  addStepCard(slide, 4, 9.7, 4.18, 2.36, 1.12, "增强监管可视化", "把告警趋势、区域分布和异常模式转为更强的数据分析界面。");
  finalize(slide);
}

// 16. Conclusion
if (includeSlide(16)) {
  const slide = pptx.addSlide();
  addBase(slide, "12 总结", 16);
  slide.addShape(pptx.ShapeType.rect, {
    x: 0.95,
    y: 1.25,
    w: 0.12,
    h: 4.25,
    line: { color: COLORS.blue, transparency: 100 },
    fill: { color: COLORS.blue },
  });
  slide.addText("总结", {
    x: 1.28,
    y: 1.2,
    w: 1.0,
    h: 0.32,
    fontFace: "DengXian",
    fontSize: 24,
    bold: true,
    color: COLORS.ink,
    margin: 0,
  });
  slide.addText("本课题完成了可溯源水果商城系统的分析、设计、实现与测试，\n基本形成了“交易 + 溯源 + 验真 + 监管”的完整实现框架。", {
    x: 1.32,
    y: 1.78,
    w: 5.8,
    h: 0.7,
    fontSize: 14,
    color: COLORS.ink,
    margin: 0,
  });
  addMetric(slide, 1.32, 3.05, 2.1, 1.22, "贡献一", "交易闭环", "商品、下单、支付与库存状态流转已贯通", COLORS.softBlue);
  addMetric(slide, 3.65, 3.05, 2.1, 1.22, "贡献二", "追溯闭环", "批次、事件、traceId 与时间线聚合查询已形成", COLORS.softDark);
  addMetric(slide, 5.98, 3.05, 2.1, 1.22, "贡献三", "可信闭环", "签名、锚点、风控、告警与审计协同落地", COLORS.softBlue);
  addPanel(slide, 8.7, 1.62, 3.45, 3.75, { fill: COLORS.panel });
  slide.addText("答辩结束", {
    x: 9.18,
    y: 2.28,
    w: 2.5,
    h: 0.42,
    fontFace: "DengXian",
    fontSize: 23,
    bold: true,
    color: COLORS.ink,
    align: "center",
    margin: 0,
  });
  slide.addText("感谢各位老师批评指正", {
    x: 9.1,
    y: 3.02,
    w: 2.66,
    h: 0.24,
    fontSize: 11,
    color: COLORS.blue,
    align: "center",
    margin: 0,
  });
  slide.addText("可溯源的水果商城设计与实现", {
    x: 8.98,
    y: 4.2,
    w: 2.9,
    h: 0.2,
    fontSize: 9.4,
    color: COLORS.muted,
    align: "center",
    margin: 0,
  });
  finalize(slide);
}

const output = path.join(__dirname, outputName);

pptx
  .writeFile({ fileName: output })
  .then(() => {
    console.log(output);
  })
  .catch((err) => {
    console.error(err);
    process.exitCode = 1;
  });
