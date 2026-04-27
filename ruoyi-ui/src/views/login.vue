<template>
  <div class="login">
    <div class="login-shell">
      <section class="brand-panel">
        <div class="brand-panel__inner">
          <span class="brand-panel__eyebrow">Repair Shop Operations</span>
          <h1 class="brand-panel__title">{{ title }}</h1>
          <p class="brand-panel__subtitle">
            围绕门店经营、财务收支、挂账跟进与工单协同组织统一入口，让老板、店长、财务和门店员工更快进入日常业务后台。
          </p>

          <div class="positioning-tags">
            <span v-for="tag in positioningTags" :key="tag">{{ tag }}</span>
          </div>

          <div class="info-list">
            <article v-for="item in helperCards" :key="item.title" class="info-card">
              <span class="info-card__index">{{ item.index }}</span>
              <div class="info-card__content">
                <strong>{{ item.title }}</strong>
                <p>{{ item.desc }}</p>
              </div>
            </article>
          </div>

          <div class="security-note">
            <span class="security-note__label">安全提醒</span>
            <p>请仅在可信设备勾选记住密码，离岗或交班前及时退出系统。</p>
          </div>
        </div>
      </section>

      <section class="form-panel">
        <div class="form-panel__header">
          <span class="form-panel__badge">业务后台登录</span>
          <h2>欢迎登录</h2>
          <p>输入账号信息后即可进入门店日常管理工作台。</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入登录账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入登录密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item v-if="captchaEnabled" prop="code" class="captcha-item">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="请输入验证码"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <button type="button" class="captcha-button" @click="getCode">
                <img :src="codeUrl" alt="验证码" class="login-code-img">
              </button>
            </div>
          </el-form-item>

          <div class="form-toolbar">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <span class="form-toolbar__tip">建议仅在个人办公设备使用</span>
          </div>

          <el-form-item class="submit-item">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              class="login-submit"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">进入系统</span>
              <span v-else>登录中...</span>
            </el-button>

            <div v-if="register" class="register-entry">
              <router-link class="link-type" :to="'/register'">立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>
      </section>
    </div>

    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from "@/utils/jsencrypt"

export default {
  name: "Login",
  data() {
    return {
      title: "修理厂管理系统",
      // 登录页品牌文案在本地维护，避免本次改动影响全局 footer 配置。
      footerContent: "修理厂管理系统 Copyright © 2026 All Rights Reserved.",
      codeUrl: "",
      positioningTags: ["门店经营", "财务收支", "挂账跟进", "工单协同"],
      helperCards: [
        {
          index: "01",
          title: "适用角色",
          desc: "老板、店长、财务与门店员工从同一入口进入业务后台，减少日常切换成本。"
        },
        {
          index: "02",
          title: "登录说明",
          desc: "验证码开启时请按页面展示输入，若看不清可点击图片刷新后重新登录。"
        },
        {
          index: "03",
          title: "操作提醒",
          desc: "登录失败时系统会延续现有逻辑刷新验证码，请核对账号、密码和验证码后再提交。"
        }
      ],
      loginForm: {
        // 去掉默认演示账号，避免正式业务系统初始态看起来像测试环境。
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入登录账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入登录密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      // 验证码仍复用现有接口返回，不调整登录请求结构和页面开关逻辑。
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get("rememberMe")
      this.loginForm = {
        ...this.loginForm,
        username: username === undefined ? "" : username,
        password: password === undefined ? "" : decrypt(password),
        rememberMe: rememberMe === undefined ? false : rememberMe === "true"
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set("rememberMe", this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove("rememberMe")
          }
          // 登录提交继续走现有 store 登录流程，失败后的验证码刷新逻辑保持不变。
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(() => {})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login {
  --login-ink: #18333a;
  --login-ink-soft: #51686d;
  --login-surface: rgba(248, 244, 237, 0.92);
  --login-line: rgba(24, 51, 58, 0.12);
  --login-gold: #d5a24c;
  --login-gold-deep: #9a6731;
  --login-teal: #234850;
  --login-shadow: 0 24px 60px rgba(27, 44, 48, 0.18);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  overflow: hidden;
  padding: 56px 24px 88px;
  background:
    radial-gradient(circle at top left, rgba(215, 162, 76, 0.16), transparent 34%),
    radial-gradient(circle at right center, rgba(35, 72, 80, 0.18), transparent 30%),
    linear-gradient(135deg, #eef0ea 0%, #f7f2e8 46%, #e7ece8 100%);
  color: var(--login-ink);
  font-family: "Avenir Next", "PingFang SC", "Microsoft YaHei", sans-serif;

  &::before,
  &::after {
    content: "";
    position: absolute;
    inset: 0;
    pointer-events: none;
  }

  &::before {
    background-image:
      linear-gradient(rgba(24, 51, 58, 0.04) 1px, transparent 1px),
      linear-gradient(90deg, rgba(24, 51, 58, 0.04) 1px, transparent 1px);
    background-size: 32px 32px;
    mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.52), transparent 90%);
  }

  &::after {
    background:
      radial-gradient(circle at 18% 22%, rgba(255, 255, 255, 0.65), transparent 24%),
      radial-gradient(circle at 84% 18%, rgba(255, 255, 255, 0.52), transparent 20%);
  }
}

.login-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(360px, 420px);
  width: 100%;
  max-width: 1160px;
  border: 1px solid rgba(255, 255, 255, 0.65);
  border-radius: 28px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.48);
  box-shadow: var(--login-shadow);
  backdrop-filter: blur(12px);
}

.brand-panel {
  position: relative;
  padding: 56px 56px 48px;
  background:
    linear-gradient(160deg, rgba(25, 51, 58, 0.96), rgba(30, 62, 69, 0.88)),
    linear-gradient(135deg, #18333a, #2f4f57);
  color: #f9f3e7;

  &::after {
    content: "";
    position: absolute;
    right: -72px;
    bottom: -72px;
    width: 240px;
    height: 240px;
    border-radius: 50%;
    border: 1px solid rgba(244, 221, 176, 0.16);
    background: radial-gradient(circle, rgba(244, 221, 176, 0.16), transparent 70%);
  }
}

.brand-panel__inner {
  position: relative;
  z-index: 1;
  max-width: 560px;
}

.brand-panel__eyebrow {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 14px;
  border: 1px solid rgba(249, 243, 231, 0.14);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #f2d8a2;
  font-size: 12px;
  letter-spacing: 1.8px;
  text-transform: uppercase;
}

.brand-panel__title {
  margin: 24px 0 18px;
  color: #fff8ec;
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 42px;
  font-weight: 600;
  line-height: 1.18;
}

.brand-panel__subtitle {
  max-width: 520px;
  margin: 0;
  color: rgba(255, 248, 236, 0.82);
  font-size: 15px;
  line-height: 1.9;
}

.positioning-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 28px;

  span {
    display: inline-flex;
    align-items: center;
    min-height: 34px;
    padding: 0 14px;
    border-radius: 999px;
    background: rgba(244, 221, 176, 0.1);
    color: #f4ddb0;
    font-size: 13px;
    letter-spacing: 0.6px;
  }
}

.info-list {
  display: grid;
  gap: 16px;
  margin-top: 34px;
}

.info-card {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr);
  gap: 16px;
  padding: 18px 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.06);
}

.info-card__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: rgba(213, 162, 76, 0.16);
  color: #f4ddb0;
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 20px;
}

.info-card__content {
  strong {
    display: block;
    color: #fff8ec;
    font-size: 16px;
    font-weight: 600;
  }

  p {
    margin: 10px 0 0;
    color: rgba(255, 248, 236, 0.74);
    font-size: 13px;
    line-height: 1.75;
  }
}

.security-note {
  margin-top: 18px;
  padding: 16px 18px;
  border-left: 3px solid rgba(213, 162, 76, 0.88);
  background: rgba(255, 255, 255, 0.05);

  p {
    margin: 8px 0 0;
    color: rgba(255, 248, 236, 0.78);
    font-size: 13px;
    line-height: 1.7;
  }
}

.security-note__label {
  color: #f4ddb0;
  font-size: 12px;
  letter-spacing: 1px;
}

.form-panel {
  padding: 52px 42px 40px;
  background: linear-gradient(180deg, rgba(251, 249, 244, 0.96), rgba(245, 243, 237, 0.94));
}

.form-panel__header {
  margin-bottom: 28px;

  h2 {
    margin: 16px 0 10px;
    color: var(--login-ink);
    font-family: "Noto Serif SC", "STSong", serif;
    font-size: 30px;
    font-weight: 600;
  }

  p {
    margin: 0;
    color: var(--login-ink-soft);
    font-size: 14px;
    line-height: 1.8;
  }
}

.form-panel__badge {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 12px;
  border: 1px solid rgba(154, 103, 49, 0.16);
  border-radius: 999px;
  background: rgba(213, 162, 76, 0.12);
  color: var(--login-gold-deep);
  font-size: 12px;
  letter-spacing: 0.8px;
}

.login-form {
  .el-form-item {
    margin-bottom: 18px;
  }

  .el-input__icon {
    color: #6c8085;
  }

  .el-input {
    width: 100%;
  }

  .el-input__inner {
    height: 48px;
    border: 1px solid rgba(24, 51, 58, 0.14);
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.78);
    color: var(--login-ink);
    transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
  }

  .el-input__inner:focus {
    border-color: rgba(213, 162, 76, 0.88);
    background: #fff;
    box-shadow: 0 0 0 4px rgba(213, 162, 76, 0.12);
  }
}

.input-icon {
  width: 16px;
  margin-left: 2px;
}

.captcha-item {
  margin-bottom: 22px;
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 122px;
  gap: 12px;
  align-items: stretch;
}

.captcha-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 122px;
  min-height: 48px;
  padding: 0;
  border: 1px solid rgba(24, 51, 58, 0.12);
  border-radius: 14px;
  background: #f3eee4;
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    border-color: rgba(213, 162, 76, 0.8);
    box-shadow: 0 10px 20px rgba(27, 44, 48, 0.08);
    transform: translateY(-1px);
  }
}

.login-code-img {
  display: block;
  width: 100%;
  height: 46px;
  border-radius: 12px;
  object-fit: cover;
}

.form-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
  color: var(--login-ink-soft);
  font-size: 13px;
}

.form-toolbar ::v-deep .el-checkbox__label {
  color: var(--login-ink);
}

.form-toolbar ::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: var(--login-ink);
}

.form-toolbar ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner,
.form-toolbar ::v-deep .el-checkbox__input.is-indeterminate .el-checkbox__inner {
  border-color: var(--login-gold);
  background-color: var(--login-gold);
}

.form-toolbar__tip {
  text-align: right;
}

.submit-item {
  margin-bottom: 0;
}

.login-submit {
  width: 100%;
  min-height: 48px;
  border-color: transparent;
  border-radius: 14px;
  background: linear-gradient(135deg, #234850, #3a6168);
  box-shadow: 0 16px 28px rgba(35, 72, 80, 0.18);
  font-size: 15px;
  letter-spacing: 1px;
  transition: transform 0.2s ease, box-shadow 0.2s ease, filter 0.2s ease;

  &:hover,
  &:focus {
    border-color: transparent;
    background: linear-gradient(135deg, #234850, #3a6168);
    box-shadow: 0 18px 32px rgba(35, 72, 80, 0.24);
    transform: translateY(-1px);
    filter: brightness(1.03);
  }
}

.register-entry {
  margin-top: 16px;
  text-align: right;

  .link-type {
    color: var(--login-gold-deep);
    font-size: 13px;
  }
}

.el-login-footer {
  position: fixed;
  right: 0;
  bottom: 24px;
  left: 0;
  z-index: 1;
  text-align: center;
  color: rgba(24, 51, 58, 0.74);
  font-size: 12px;
  letter-spacing: 1px;
}

@media (max-width: 1080px) {
  .login {
    padding: 32px 18px 78px;
  }

  .login-shell {
    grid-template-columns: 1fr;
    max-width: 720px;
  }

  .brand-panel,
  .form-panel {
    padding-right: 36px;
    padding-left: 36px;
  }
}

@media (max-width: 768px) {
  .login {
    padding: 18px 14px 72px;
  }

  .login-shell {
    border-radius: 22px;
  }

  .brand-panel,
  .form-panel {
    padding: 28px 22px;
  }

  .brand-panel__title {
    font-size: 32px;
  }

  .form-panel__header h2 {
    font-size: 26px;
  }

  .info-card {
    grid-template-columns: 44px minmax(0, 1fr);
    padding: 16px;
  }

  .info-card__index {
    width: 44px;
    height: 44px;
    border-radius: 14px;
    font-size: 18px;
  }

  .captcha-row {
    grid-template-columns: 1fr;
  }

  .captcha-button {
    width: 100%;
  }

  .form-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .form-toolbar__tip {
    text-align: left;
  }

  .el-login-footer {
    bottom: 16px;
    padding: 0 20px;
    line-height: 1.6;
  }
}
</style>
