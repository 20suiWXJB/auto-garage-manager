<template>
  <div class="app-container home-lite">
    <div class="home-shell">
      <section class="hero-panel">
        <div class="hero-copy">
          <div class="eyebrow">
            <span class="eyebrow-mark"></span>
            今日工作台
          </div>
          <h1>修理厂管理系统</h1>
          <p class="hero-desc">
            {{ welcomeMessage }}
          </p>
          <div class="hero-status">
            <span class="status-pill" :class="{ private: privacyMode }">
              {{ privacyMode ? '隐私模式已开启' : '经营概览已开启' }}
            </span>
            <span class="status-note">
              {{ privacyMode ? '默认隐藏首页运营数据' : '已展示精简后的首页经营概览' }}
            </span>
          </div>
        </div>

        <div class="hero-side">
          <div class="privacy-card">
            <div class="privacy-copy">
              <span>首页隐私模式</span>
              <strong>{{ privacyMode ? '隐藏运营数据' : '显示运营数据' }}</strong>
              <p>仅影响首页展示，切换状态会保存在当前浏览器。</p>
            </div>
            <el-switch
              v-model="privacyMode"
              active-color="#1f4a46"
              inactive-color="#d89a47"
              @change="handlePrivacyChange"
            />
          </div>

          <div class="entry-grid">
            <button
              v-for="entry in quickEntryCards"
              :key="entry.title"
              type="button"
              class="entry-card"
              :class="{ disabled: entry.disabled }"
              @click="handleQuickEntry(entry)"
            >
              <div class="entry-top">
                <i :class="entry.icon"></i>
                <el-tag size="mini" :type="entry.disabled ? 'info' : 'success'" effect="plain">
                  {{ entry.disabled ? entry.disabledText : '常用' }}
                </el-tag>
              </div>
              <strong>{{ entry.title }}</strong>
              <p>{{ entry.desc }}</p>
            </button>
          </div>
        </div>
      </section>

      <section class="workspace-panel">
        <div class="workspace-main" v-loading="loadingStats && !privacyMode">
          <div class="section-head">
            <div>
              <span>今日工作区</span>
              <small>首页只保留必要概览与安排，减少无关干扰。</small>
            </div>
            <el-tag v-if="!privacyMode && statsError" size="mini" type="warning" effect="plain">
              统计获取失败，已回退为空态
            </el-tag>
          </div>

          <div v-if="privacyMode" class="privacy-placeholder">
            <div class="placeholder-icon">
              <i class="el-icon-view"></i>
            </div>
            <strong>当前为隐私模式，运营数据已隐藏</strong>
            <p>
              首页仍保留常用入口和今日安排，方便在公共场景下快速进入业务页面处理当天事项。
            </p>
          </div>

          <div v-else class="overview-panel">
            <div class="overview-copy">
              <span>本月收支差额</span>
              <strong :class="{ danger: revenueBalance < 0 }">
                {{ formatAmount(revenueBalance) }}
              </strong>
              <p>{{ balanceHint }}</p>
            </div>

            <div class="overview-metrics">
              <div v-for="item in overviewItems" :key="item.label" class="metric-card">
                <div class="metric-icon">
                  <i :class="item.icon"></i>
                </div>
                <div class="metric-body">
                  <span>{{ item.label }}</span>
                  <strong>{{ formatAmount(item.value) }}</strong>
                  <small>{{ item.note }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="workspace-side">
          <div class="section-head">
            <div>
              <span>今日安排</span>
              <small>不展示金额，也能快速明确今天该做什么。</small>
            </div>
          </div>

          <div class="agenda-list">
            <div v-for="item in agendaCards" :key="item.title" class="agenda-item">
              <div class="agenda-icon">
                <i :class="item.icon"></i>
              </div>
              <div class="agenda-body">
                <strong>{{ item.title }}</strong>
                <p>{{ item.desc }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { statsBookkeeping } from '@/api/finance/bookkeeping'
import cache from '@/plugins/cache'

const HOME_PRIVACY_MODE_KEY = 'home-privacy-mode'

function createEmptyStats() {
  return {
    todayIncome: 0,
    monthIncome: 0,
    monthExpense: 0,
    currentDebtAmount: 0
  }
}

export default {
  name: 'Index',
  data() {
    return {
      privacyMode: true,
      loadingStats: false,
      statsError: false,
      stats: createEmptyStats(),
      quickEntries: [
        {
          title: '记账管理',
          desc: '查看收支流水、挂账和收款进度',
          icon: 'el-icon-document',
          path: '/finance/bookkeeping',
          permission: 'finance:bookkeeping:list'
        },
        {
          title: '资金账户管理',
          desc: '维护现金、微信、支付宝和银行卡账户',
          icon: 'el-icon-money',
          path: '/finance/account',
          permission: 'finance:account:list'
        },
        {
          title: '客户管理',
          desc: '沉淀客户档案、联系方式和回访记录',
          icon: 'el-icon-user',
          comingSoon: true
        },
        {
          title: '维修工单',
          desc: '统一安排接车、报价、施工和交付流程',
          icon: 'el-icon-s-order',
          comingSoon: true
        }
      ]
    }
  },
  computed: {
    displayName() {
      return this.$store.getters.nickName || this.$store.getters.name || '店长'
    },
    welcomeMessage() {
      return `${this.displayName}，欢迎回来。首页已调整为更轻量的单屏布局，优先保留常用入口与今天要处理的事情。`
    },
    revenueBalance() {
      return Number(this.stats.monthIncome || 0) - Number(this.stats.monthExpense || 0)
    },
    balanceHint() {
      if (this.statsError) {
        return '当前未成功获取经营统计，首页已回退为空态，你仍可继续进入业务页面处理工作。'
      }
      if (this.revenueBalance < 0) {
        return '本月支出已经高于收入，建议优先检查挂账回收和费用控制。'
      }
      if (Number(this.stats.currentDebtAmount || 0) > 0) {
        return '本月整体仍有结余，但存在挂账待收，建议安排客户跟进。'
      }
      return '本月收支节奏平稳，可以继续关注高频项目和客户复购。'
    },
    quickEntryCards() {
      return this.quickEntries.map(item => {
        if (item.comingSoon) {
          return Object.assign({}, item, {
            disabled: true,
            disabledText: '即将上线'
          })
        }
        if (!this.hasPermission(item.permission)) {
          return Object.assign({}, item, {
            disabled: true,
            disabledText: '暂无权限'
          })
        }
        return Object.assign({}, item, {
          disabled: false,
          disabledText: ''
        })
      })
    },
    overviewItems() {
      return [
        {
          label: '今日收入',
          value: this.stats.todayIncome,
          note: '确认当天收款与记账录入是否同步。',
          icon: 'el-icon-money'
        },
        {
          label: '本月收入',
          value: this.stats.monthIncome,
          note: '快速查看本月整体营收状态。',
          icon: 'el-icon-s-data'
        },
        {
          label: '当前挂账',
          value: this.stats.currentDebtAmount,
          note: '集中识别待跟进的未结清款项。',
          icon: 'el-icon-warning-outline'
        }
      ]
    },
    agendaCards() {
      return [
        {
          title: '先处理新增记账',
          desc: '确认今天的收支、收款和挂账已经及时录入，避免晚些时候集中补录。',
          icon: 'el-icon-edit-outline'
        },
        {
          title: '核对账户变动',
          desc: '检查现金、微信、支付宝和银行卡账户是否需要同步调整。',
          icon: 'el-icon-money'
        },
        {
          title: '安排客户跟进',
          desc: '把挂账、回访和待确认事项拉出来处理，减少跨天遗留。',
          icon: 'el-icon-phone-outline'
        },
        {
          title: '进入常用业务页',
          desc: '首页只做导航与概览，详细操作直接进入业务页面完成。',
          icon: 'el-icon-position'
        }
      ]
    }
  },
  created() {
    this.initHomeState()
  },
  methods: {
    createEmptyStats() {
      return createEmptyStats()
    },
    initHomeState() {
      const savedMode = cache.local.get(HOME_PRIVACY_MODE_KEY)
      if (savedMode === 'true') {
        this.privacyMode = true
      } else if (savedMode === 'false') {
        this.privacyMode = false
      } else {
        this.privacyMode = true
        cache.local.set(HOME_PRIVACY_MODE_KEY, 'true')
      }

      if (!this.privacyMode) {
        this.getStats()
      }
    },
    handlePrivacyChange(value) {
      cache.local.set(HOME_PRIVACY_MODE_KEY, value ? 'true' : 'false')
      if (value) {
        this.loadingStats = false
        this.statsError = false
        this.stats = this.createEmptyStats()
        return
      }
      this.getStats()
    },
    getStats() {
      if (this.privacyMode) {
        return
      }
      this.loadingStats = true
      this.statsError = false
      statsBookkeeping().then(response => {
        const payload = response.data || {}
        this.stats = Object.assign(this.createEmptyStats(), payload)
      }).catch(() => {
        this.statsError = true
        this.stats = this.createEmptyStats()
      }).then(() => {
        this.loadingStats = false
      })
    },
    hasPermission(permission) {
      const permissions = this.$store.getters.permissions || []
      return permissions.indexOf('*:*:*') !== -1 || permissions.indexOf(permission) !== -1
    },
    handleQuickEntry(entry) {
      if (entry.disabled) {
        if (entry.comingSoon) {
          this.$message.info('该功能正在规划中，首页先展示能力入口。')
          return
        }
        this.$message.warning('当前账号暂无该功能权限。')
        return
      }
      this.$router.push(entry.path)
    },
    formatAmount(value) {
      return '¥' + Number(value || 0).toFixed(2)
    }
  }
}
</script>

<style scoped lang="scss">
.home-lite {
  min-height: calc(100vh - 84px);
  padding-bottom: 24px;
  color: #253238;
  background:
    radial-gradient(circle at top left, rgba(216, 154, 71, 0.14), transparent 30%),
    radial-gradient(circle at bottom right, rgba(31, 74, 70, 0.12), transparent 28%),
    linear-gradient(180deg, #f6f1e8 0%, #edf1eb 100%);
}

.home-shell {
  display: grid;
  gap: 18px;
}

.hero-panel,
.workspace-panel {
  border: 1px solid rgba(48, 63, 67, 0.08);
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 22px 48px rgba(43, 56, 60, 0.08);
}

.hero-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  background:
    linear-gradient(140deg, rgba(18, 38, 42, 0.96), rgba(31, 74, 70, 0.92)),
    linear-gradient(140deg, #14292f, #2c514c);
}

.hero-copy,
.hero-side,
.workspace-main,
.workspace-side {
  position: relative;
  padding: 28px;
}

.hero-copy::after {
  position: absolute;
  right: 0;
  top: 28px;
  bottom: 28px;
  width: 1px;
  background: linear-gradient(180deg, transparent, rgba(255, 248, 236, 0.18), transparent);
  content: '';
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  border: 1px solid rgba(242, 204, 141, 0.3);
  border-radius: 999px;
  font-size: 12px;
  letter-spacing: 0.16em;
  color: #f1d6a6;
}

.eyebrow-mark {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #d89a47;
  box-shadow: 0 0 0 6px rgba(216, 154, 71, 0.14);
}

.hero-copy h1 {
  margin: 22px 0 14px;
  font-family: "Noto Serif SC", "Songti SC", "STSong", serif;
  font-size: 40px;
  line-height: 1.12;
  font-weight: 700;
  color: #fff7eb;
}

.hero-desc {
  max-width: 620px;
  margin: 0;
  line-height: 1.9;
  color: rgba(255, 247, 235, 0.8);
}

.hero-status {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-top: 26px;
}

.status-pill,
.status-note {
  display: inline-flex;
  align-items: center;
  min-height: 36px;
  padding: 0 14px;
  border-radius: 999px;
  font-size: 13px;
}

.status-pill {
  color: #1d3438;
  background: #f1d6a6;
}

.status-pill.private {
  color: #f6f8f6;
  background: rgba(255, 255, 255, 0.12);
}

.status-note {
  color: rgba(255, 247, 235, 0.72);
  background: rgba(255, 255, 255, 0.08);
}

.hero-side {
  display: grid;
  gap: 16px;
  background: rgba(255, 255, 255, 0.04);
}

.privacy-card,
.entry-card,
.overview-panel,
.agenda-item,
.privacy-placeholder {
  border: 1px solid rgba(49, 64, 68, 0.08);
  border-radius: 22px;
}

.privacy-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  background: rgba(246, 241, 232, 0.92);
}

.privacy-copy span,
.metric-body span,
.section-head small {
  display: block;
  font-size: 12px;
  color: #748489;
}

.privacy-copy strong,
.overview-copy strong {
  display: block;
  margin: 6px 0 4px;
  color: #21353a;
}

.privacy-copy p,
.overview-copy p,
.agenda-body p,
.entry-card p,
.privacy-placeholder p {
  margin: 0;
  line-height: 1.7;
  color: #63767b;
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.entry-card {
  padding: 18px;
  background: rgba(255, 255, 255, 0.08);
  text-align: left;
  cursor: pointer;
  transition: transform 0.2s ease, background 0.2s ease, border-color 0.2s ease;
}

.entry-card:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.14);
  border-color: rgba(255, 255, 255, 0.18);
}

.entry-card.disabled,
.entry-card.disabled:hover {
  transform: none;
  cursor: not-allowed;
  background: rgba(255, 255, 255, 0.06);
}

.entry-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
}

.entry-top i {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  font-size: 21px;
  color: #17323a;
  background: #f1d6a6;
}

.entry-card strong {
  display: block;
  margin-bottom: 8px;
  font-size: 16px;
  color: #fff7eb;
}

.entry-card p {
  color: rgba(255, 247, 235, 0.74);
}

.workspace-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(320px, 0.85fr);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
}

.workspace-main {
  background: rgba(255, 255, 255, 0.72);
}

.workspace-side {
  background: linear-gradient(180deg, rgba(242, 236, 226, 0.86) 0%, rgba(236, 241, 234, 0.92) 100%);
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.section-head span {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: #203136;
}

.privacy-placeholder,
.overview-panel {
  margin-top: 22px;
}

.privacy-placeholder {
  display: grid;
  gap: 14px;
  align-content: center;
  min-height: 280px;
  padding: 28px;
  background: linear-gradient(180deg, rgba(247, 244, 238, 0.96) 0%, rgba(240, 244, 238, 0.96) 100%);
}

.placeholder-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  border-radius: 18px;
  font-size: 24px;
  color: #1f4a46;
  background: rgba(31, 74, 70, 0.1);
}

.privacy-placeholder strong {
  font-size: 22px;
  color: #203136;
}

.overview-panel {
  padding: 24px;
  background: linear-gradient(180deg, rgba(250, 248, 244, 0.98) 0%, rgba(239, 243, 236, 0.98) 100%);
}

.overview-copy {
  padding-bottom: 18px;
  border-bottom: 1px solid rgba(50, 66, 71, 0.08);
}

.overview-copy span {
  display: block;
  font-size: 13px;
  color: #6e8085;
}

.overview-copy strong {
  margin: 10px 0 12px;
  font-size: 40px;
  line-height: 1;
}

.overview-copy strong.danger {
  color: #b75a42;
}

.overview-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 20px;
}

.metric-card {
  display: flex;
  gap: 14px;
  padding: 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.68);
}

.metric-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  font-size: 20px;
  color: #1f4a46;
  background: rgba(31, 74, 70, 0.1);
}

.metric-body strong {
  display: block;
  margin: 7px 0 6px;
  font-size: 24px;
  line-height: 1.1;
  color: #22363b;
}

.metric-body small {
  display: block;
  line-height: 1.6;
  color: #66787d;
}

.agenda-list {
  display: grid;
  gap: 14px;
  margin-top: 22px;
}

.agenda-item {
  display: flex;
  gap: 14px;
  padding: 18px;
  background: rgba(255, 255, 255, 0.68);
}

.agenda-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: none;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  font-size: 20px;
  color: #8a5a21;
  background: rgba(216, 154, 71, 0.16);
}

.agenda-body strong {
  display: block;
  margin-bottom: 6px;
  font-size: 15px;
  color: #203136;
}

@media (max-width: 1280px) {
  .hero-panel,
  .workspace-panel {
    grid-template-columns: 1fr;
  }

  .hero-copy::after {
    display: none;
  }
}

@media (max-width: 768px) {
  .home-lite {
    padding-bottom: 12px;
  }

  .hero-copy,
  .hero-side,
  .workspace-main,
  .workspace-side {
    padding: 20px;
  }

  .hero-copy h1 {
    font-size: 30px;
  }

  .entry-grid,
  .overview-metrics {
    grid-template-columns: 1fr;
  }

  .privacy-card,
  .section-head {
    align-items: flex-start;
  }

  .privacy-card {
    flex-direction: column;
  }

  .overview-copy strong {
    font-size: 32px;
  }

  .privacy-placeholder {
    min-height: auto;
  }
}
</style>
