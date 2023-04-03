/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
! function(e, t) {
	"object" == typeof exports && "undefined" != typeof module ? t(exports) : "function" == typeof define && define.amd ? define(["exports"], t) : t((e = "undefined" != typeof globalThis ? globalThis : e || self).PureJSSelectors = {})
}(this, (function(e) {
	"use strict";
	var t = "undefined" != typeof globalThis ? globalThis : "undefined" != typeof window ? window : "undefined" != typeof global ? global : "undefined" != typeof self ? self : {};

	function r(e) {
		return e && e.__esModule && Object.prototype.hasOwnProperty.call(e, "default") ? e.default : e
	}

	function n(e) {
		if (e.__esModule) return e;
		var t = e.default;
		if ("function" == typeof t) {
			var r = function e() {
				if (this instanceof e) {
					var r = [null];
					return r.push.apply(r, arguments), new(Function.bind.apply(t, r))
				}
				return t.apply(this, arguments)
			};
			r.prototype = t.prototype
		} else r = {};
		return Object.defineProperty(r, "__esModule", {
			value: !0
		}), Object.keys(e).forEach((function(t) {
			var n = Object.getOwnPropertyDescriptor(e, t);
			Object.defineProperty(r, t, n.get ? n : {
				enumerable: !0,
				get: function() {
					return e[t]
				}
			})
		})), r
	}
	var o, i, a, c, s, u = {},
		l = {};

	function d() {
		if (i) return o;
		i = 1;
		var e = Object.getOwnPropertySymbols,
			t = Object.prototype.hasOwnProperty,
			r = Object.prototype.propertyIsEnumerable;
		return o = function() {
			try {
				if (!Object.assign) return !1;
				var e = new String("abc");
				if (e[5] = "de", "5" === Object.getOwnPropertyNames(e)[0]) return !1;
				for (var t = {}, r = 0; r < 10; r++) t["_" + String.fromCharCode(r)] = r;
				var n = Object.getOwnPropertyNames(t).map((function(e) {
					return t[e]
				}));
				if ("0123456789" !== n.join("")) return !1;
				var o = {};
				return "abcdefghijklmnopqrst".split("").forEach((function(e) {
					o[e] = e
				})), "abcdefghijklmnopqrst" === Object.keys(Object.assign({}, o)).join("")
			} catch (e) {
				return !1
			}
		}() ? Object.assign : function(n, o) {
			for (var i, a, c = function(e) {
					if (null == e) throw new TypeError("Object.assign cannot be called with null or undefined");
					return Object(e)
				}(n), s = 1; s < arguments.length; s++) {
				for (var u in i = Object(arguments[s])) t.call(i, u) && (c[u] = i[u]);
				if (e) {
					a = e(i);
					for (var l = 0; l < a.length; l++) r.call(i, a[l]) && (c[a[l]] = i[a[l]])
				}
			}
			return c
		}, o
	}
	/** @license React v16.14.0
	 * react.production.min.js
	 *
	 * Copyright (c) Facebook, Inc. and its affiliates.
	 *
	 * This source code is licensed under the MIT license found in the
	 * LICENSE file in the root directory of this source tree.
	 */
	function p() {
		if (a) return l;
		a = 1;
		var e = d(),
			t = "function" == typeof Symbol && Symbol.for,
			r = t ? Symbol.for("react.element") : 60103,
			n = t ? Symbol.for("react.portal") : 60106,
			o = t ? Symbol.for("react.fragment") : 60107,
			i = t ? Symbol.for("react.strict_mode") : 60108,
			c = t ? Symbol.for("react.profiler") : 60114,
			s = t ? Symbol.for("react.provider") : 60109,
			u = t ? Symbol.for("react.context") : 60110,
			p = t ? Symbol.for("react.forward_ref") : 60112,
			f = t ? Symbol.for("react.suspense") : 60113,
			m = t ? Symbol.for("react.memo") : 60115,
			g = t ? Symbol.for("react.lazy") : 60116,
			h = "function" == typeof Symbol && Symbol.iterator;

		function v(e) {
			for (var t = "https://reactjs.org/docs/error-decoder.html?invariant=" + e, r = 1; r < arguments.length; r++) t += "&args[]=" + encodeURIComponent(arguments[r]);
			return "Minified React error #" + e + "; visit " + t + " for the full message or use the non-minified dev environment for full errors and additional helpful warnings."
		}
		var b = {
				isMounted: function() {
					return !1
				},
				enqueueForceUpdate: function() {},
				enqueueReplaceState: function() {},
				enqueueSetState: function() {}
			},
			y = {};

		function _(e, t, r) {
			this.props = e, this.context = t, this.refs = y, this.updater = r || b
		}

		function w() {}

		function k(e, t, r) {
			this.props = e, this.context = t, this.refs = y, this.updater = r || b
		}
		_.prototype.isReactComponent = {}, _.prototype.setState = function(e, t) {
			if ("object" != typeof e && "function" != typeof e && null != e) throw Error(v(85));
			this.updater.enqueueSetState(this, e, t, "setState")
		}, _.prototype.forceUpdate = function(e) {
			this.updater.enqueueForceUpdate(this, e, "forceUpdate")
		}, w.prototype = _.prototype;
		var x = k.prototype = new w;
		x.constructor = k, e(x, _.prototype), x.isPureReactComponent = !0;
		var T = {
				current: null
			},
			S = Object.prototype.hasOwnProperty,
			I = {
				key: !0,
				ref: !0,
				__self: !0,
				__source: !0
			};

		function E(e, t, n) {
			var o, i = {},
				a = null,
				c = null;
			if (null != t)
				for (o in void 0 !== t.ref && (c = t.ref), void 0 !== t.key && (a = "" + t.key), t) S.call(t, o) && !I.hasOwnProperty(o) && (i[o] = t[o]);
			var s = arguments.length - 2;
			if (1 === s) i.children = n;
			else if (1 < s) {
				for (var u = Array(s), l = 0; l < s; l++) u[l] = arguments[l + 2];
				i.children = u
			}
			if (e && e.defaultProps)
				for (o in s = e.defaultProps) void 0 === i[o] && (i[o] = s[o]);
			return {
				$$typeof: r,
				type: e,
				key: a,
				ref: c,
				props: i,
				_owner: T.current
			}
		}

		function P(e) {
			return "object" == typeof e && null !== e && e.$$typeof === r
		}
		var D = /\/+/g,
			C = [];

		function A(e, t, r, n) {
			if (C.length) {
				var o = C.pop();
				return o.result = e, o.keyPrefix = t, o.func = r, o.context = n, o.count = 0, o
			}
			return {
				result: e,
				keyPrefix: t,
				func: r,
				context: n,
				count: 0
			}
		}

		function O(e) {
			e.result = null, e.keyPrefix = null, e.func = null, e.context = null, e.count = 0, 10 > C.length && C.push(e)
		}

		function B(e, t, o, i) {
			var a = typeof e;
			"undefined" !== a && "boolean" !== a || (e = null);
			var c = !1;
			if (null === e) c = !0;
			else switch (a) {
				case "string":
				case "number":
					c = !0;
					break;
				case "object":
					switch (e.$$typeof) {
						case r:
						case n:
							c = !0
					}
			}
			if (c) return o(i, e, "" === t ? "." + M(e, 0) : t), 1;
			if (c = 0, t = "" === t ? "." : t + ":", Array.isArray(e))
				for (var s = 0; s < e.length; s++) {
					var u = t + M(a = e[s], s);
					c += B(a, u, o, i)
				} else if (null === e || "object" != typeof e ? u = null : u = "function" == typeof(u = h && e[h] || e["@@iterator"]) ? u : null, "function" == typeof u)
					for (e = u.call(e), s = 0; !(a = e.next()).done;) c += B(a = a.value, u = t + M(a, s++), o, i);
				else if ("object" === a) throw o = "" + e, Error(v(31, "[object Object]" === o ? "object with keys {" + Object.keys(e).join(", ") + "}" : o, ""));
			return c
		}

		function R(e, t, r) {
			return null == e ? 0 : B(e, "", t, r)
		}

		function M(e, t) {
			return "object" == typeof e && null !== e && null != e.key ? function(e) {
				var t = {
					"=": "=0",
					":": "=2"
				};
				return "$" + ("" + e).replace(/[=:]/g, (function(e) {
					return t[e]
				}))
			}(e.key) : t.toString(36)
		}

		function z(e, t) {
			e.func.call(e.context, t, e.count++)
		}

		function F(e, t, n) {
			var o = e.result,
				i = e.keyPrefix;
			e = e.func.call(e.context, t, e.count++), Array.isArray(e) ? j(e, o, n, (function(e) {
				return e
			})) : null != e && (P(e) && (e = function(e, t) {
				return {
					$$typeof: r,
					type: e.type,
					key: t,
					ref: e.ref,
					props: e.props,
					_owner: e._owner
				}
			}(e, i + (!e.key || t && t.key === e.key ? "" : ("" + e.key).replace(D, "$&/") + "/") + n)), o.push(e))
		}

		function j(e, t, r, n, o) {
			var i = "";
			null != r && (i = ("" + r).replace(D, "$&/") + "/"), R(e, F, t = A(t, i, n, o)), O(t)
		}
		var L = {
			current: null
		};

		function U() {
			var e = L.current;
			if (null === e) throw Error(v(321));
			return e
		}
		var N = {
			ReactCurrentDispatcher: L,
			ReactCurrentBatchConfig: {
				suspense: null
			},
			ReactCurrentOwner: T,
			IsSomeRendererActing: {
				current: !1
			},
			assign: e
		};
		return l.Children = {
			map: function(e, t, r) {
				if (null == e) return e;
				var n = [];
				return j(e, n, null, t, r), n
			},
			forEach: function(e, t, r) {
				if (null == e) return e;
				R(e, z, t = A(null, null, t, r)), O(t)
			},
			count: function(e) {
				return R(e, (function() {
					return null
				}), null)
			},
			toArray: function(e) {
				var t = [];
				return j(e, t, null, (function(e) {
					return e
				})), t
			},
			only: function(e) {
				if (!P(e)) throw Error(v(143));
				return e
			}
		}, l.Component = _, l.Fragment = o, l.Profiler = c, l.PureComponent = k, l.StrictMode = i, l.Suspense = f, l.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED = N, l.cloneElement = function(t, n, o) {
			if (null == t) throw Error(v(267, t));
			var i = e({}, t.props),
				a = t.key,
				c = t.ref,
				s = t._owner;
			if (null != n) {
				if (void 0 !== n.ref && (c = n.ref, s = T.current), void 0 !== n.key && (a = "" + n.key), t.type && t.type.defaultProps) var u = t.type.defaultProps;
				for (l in n) S.call(n, l) && !I.hasOwnProperty(l) && (i[l] = void 0 === n[l] && void 0 !== u ? u[l] : n[l])
			}
			var l = arguments.length - 2;
			if (1 === l) i.children = o;
			else if (1 < l) {
				u = Array(l);
				for (var d = 0; d < l; d++) u[d] = arguments[d + 2];
				i.children = u
			}
			return {
				$$typeof: r,
				type: t.type,
				key: a,
				ref: c,
				props: i,
				_owner: s
			}
		}, l.createContext = function(e, t) {
			return void 0 === t && (t = null), (e = {
				$$typeof: u,
				_calculateChangedBits: t,
				_currentValue: e,
				_currentValue2: e,
				_threadCount: 0,
				Provider: null,
				Consumer: null
			}).Provider = {
				$$typeof: s,
				_context: e
			}, e.Consumer = e
		}, l.createElement = E, l.createFactory = function(e) {
			var t = E.bind(null, e);
			return t.type = e, t
		}, l.createRef = function() {
			return {
				current: null
			}
		}, l.forwardRef = function(e) {
			return {
				$$typeof: p,
				render: e
			}
		}, l.isValidElement = P, l.lazy = function(e) {
			return {
				$$typeof: g,
				_ctor: e,
				_status: -1,
				_result: null
			}
		}, l.memo = function(e, t) {
			return {
				$$typeof: m,
				type: e,
				compare: void 0 === t ? null : t
			}
		}, l.useCallback = function(e, t) {
			return U().useCallback(e, t)
		}, l.useContext = function(e, t) {
			return U().useContext(e, t)
		}, l.useDebugValue = function() {}, l.useEffect = function(e, t) {
			return U().useEffect(e, t)
		}, l.useImperativeHandle = function(e, t, r) {
			return U().useImperativeHandle(e, t, r)
		}, l.useLayoutEffect = function(e, t) {
			return U().useLayoutEffect(e, t)
		}, l.useMemo = function(e, t) {
			return U().useMemo(e, t)
		}, l.useReducer = function(e, t, r) {
			return U().useReducer(e, t, r)
		}, l.useRef = function(e) {
			return U().useRef(e)
		}, l.useState = function(e) {
			return U().useState(e)
		}, l.version = "16.14.0", l
	}! function(e) {
		e.exports = p()
	}({
		get exports() {
			return u
		},
		set exports(e) {
			u = e
		}
	});
	var f, m, g, h = r(u),
		v = {},
		b = {},
		y = {},
		_ = {
			get exports() {
				return y
			},
			set exports(e) {
				y = e
			}
		},
		w = {};

	function k() {
		return m || (m = 1, function(e) {
			e.exports = (f || (f = 1, function(e) {
				var t, r, n, o, i;
				if ("undefined" == typeof window || "function" != typeof MessageChannel) {
					var a = null,
						c = null,
						s = function() {
							if (null !== a) try {
								var t = e.unstable_now();
								a(!0, t), a = null
							} catch (e) {
								throw setTimeout(s, 0), e
							}
						},
						u = Date.now();
					e.unstable_now = function() {
						return Date.now() - u
					}, t = function(e) {
						null !== a ? setTimeout(t, 0, e) : (a = e, setTimeout(s, 0))
					}, r = function(e, t) {
						c = setTimeout(e, t)
					}, n = function() {
						clearTimeout(c)
					}, o = function() {
						return !1
					}, i = e.unstable_forceFrameRate = function() {}
				} else {
					var l = window.performance,
						d = window.Date,
						p = window.setTimeout,
						f = window.clearTimeout;
					if ("undefined" != typeof console) {
						var m = window.cancelAnimationFrame;
						"function" != typeof window.requestAnimationFrame && console.error("This browser doesn't support requestAnimationFrame. Make sure that you load a polyfill in older browsers. https://fb.me/react-polyfills"), "function" != typeof m && console.error("This browser doesn't support cancelAnimationFrame. Make sure that you load a polyfill in older browsers. https://fb.me/react-polyfills")
					}
					if ("object" == typeof l && "function" == typeof l.now) e.unstable_now = function() {
						return l.now()
					};
					else {
						var g = d.now();
						e.unstable_now = function() {
							return d.now() - g
						}
					}
					var h = !1,
						v = null,
						b = -1,
						y = 5,
						_ = 0;
					o = function() {
						return e.unstable_now() >= _
					}, i = function() {}, e.unstable_forceFrameRate = function(e) {
						0 > e || 125 < e ? console.error("forceFrameRate takes a positive int between 0 and 125, forcing framerates higher than 125 fps is not unsupported") : y = 0 < e ? Math.floor(1e3 / e) : 5
					};
					var w = new MessageChannel,
						k = w.port2;
					w.port1.onmessage = function() {
						if (null !== v) {
							var t = e.unstable_now();
							_ = t + y;
							try {
								v(!0, t) ? k.postMessage(null) : (h = !1, v = null)
							} catch (e) {
								throw k.postMessage(null), e
							}
						} else h = !1
					}, t = function(e) {
						v = e, h || (h = !0, k.postMessage(null))
					}, r = function(t, r) {
						b = p((function() {
							t(e.unstable_now())
						}), r)
					}, n = function() {
						f(b), b = -1
					}
				}

				function x(e, t) {
					var r = e.length;
					e.push(t);
					e: for (;;) {
						var n = r - 1 >>> 1,
							o = e[n];
						if (!(void 0 !== o && 0 < I(o, t))) break e;
						e[n] = t, e[r] = o, r = n
					}
				}

				function T(e) {
					return void 0 === (e = e[0]) ? null : e
				}

				function S(e) {
					var t = e[0];
					if (void 0 !== t) {
						var r = e.pop();
						if (r !== t) {
							e[0] = r;
							e: for (var n = 0, o = e.length; n < o;) {
								var i = 2 * (n + 1) - 1,
									a = e[i],
									c = i + 1,
									s = e[c];
								if (void 0 !== a && 0 > I(a, r)) void 0 !== s && 0 > I(s, a) ? (e[n] = s, e[c] = r, n = c) : (e[n] = a, e[i] = r, n = i);
								else {
									if (!(void 0 !== s && 0 > I(s, r))) break e;
									e[n] = s, e[c] = r, n = c
								}
							}
						}
						return t
					}
					return null
				}

				function I(e, t) {
					var r = e.sortIndex - t.sortIndex;
					return 0 !== r ? r : e.id - t.id
				}
				var E = [],
					P = [],
					D = 1,
					C = null,
					A = 3,
					O = !1,
					B = !1,
					R = !1;

				function M(e) {
					for (var t = T(P); null !== t;) {
						if (null === t.callback) S(P);
						else {
							if (!(t.startTime <= e)) break;
							S(P), t.sortIndex = t.expirationTime, x(E, t)
						}
						t = T(P)
					}
				}

				function z(e) {
					if (R = !1, M(e), !B)
						if (null !== T(E)) B = !0, t(F);
						else {
							var n = T(P);
							null !== n && r(z, n.startTime - e)
						}
				}

				function F(t, i) {
					B = !1, R && (R = !1, n()), O = !0;
					var a = A;
					try {
						for (M(i), C = T(E); null !== C && (!(C.expirationTime > i) || t && !o());) {
							var c = C.callback;
							if (null !== c) {
								C.callback = null, A = C.priorityLevel;
								var s = c(C.expirationTime <= i);
								i = e.unstable_now(), "function" == typeof s ? C.callback = s : C === T(E) && S(E), M(i)
							} else S(E);
							C = T(E)
						}
						if (null !== C) var u = !0;
						else {
							var l = T(P);
							null !== l && r(z, l.startTime - i), u = !1
						}
						return u
					} finally {
						C = null, A = a, O = !1
					}
				}

				function j(e) {
					switch (e) {
						case 1:
							return -1;
						case 2:
							return 250;
						case 5:
							return 1073741823;
						case 4:
							return 1e4;
						default:
							return 5e3
					}
				}
				var L = i;
				e.unstable_IdlePriority = 5, e.unstable_ImmediatePriority = 1, e.unstable_LowPriority = 4, e.unstable_NormalPriority = 3, e.unstable_Profiling = null, e.unstable_UserBlockingPriority = 2, e.unstable_cancelCallback = function(e) {
					e.callback = null
				}, e.unstable_continueExecution = function() {
					B || O || (B = !0, t(F))
				}, e.unstable_getCurrentPriorityLevel = function() {
					return A
				}, e.unstable_getFirstCallbackNode = function() {
					return T(E)
				}, e.unstable_next = function(e) {
					switch (A) {
						case 1:
						case 2:
						case 3:
							var t = 3;
							break;
						default:
							t = A
					}
					var r = A;
					A = t;
					try {
						return e()
					} finally {
						A = r
					}
				}, e.unstable_pauseExecution = function() {}, e.unstable_requestPaint = L, e.unstable_runWithPriority = function(e, t) {
					switch (e) {
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
							break;
						default:
							e = 3
					}
					var r = A;
					A = e;
					try {
						return t()
					} finally {
						A = r
					}
				}, e.unstable_scheduleCallback = function(o, i, a) {
					var c = e.unstable_now();
					if ("object" == typeof a && null !== a) {
						var s = a.delay;
						s = "number" == typeof s && 0 < s ? c + s : c, a = "number" == typeof a.timeout ? a.timeout : j(o)
					} else a = j(o), s = c;
					return o = {
						id: D++,
						callback: i,
						priorityLevel: o,
						startTime: s,
						expirationTime: a = s + a,
						sortIndex: -1
					}, s > c ? (o.sortIndex = s, x(P, o), null === T(E) && o === T(P) && (R ? n() : R = !0, r(z, s - c))) : (o.sortIndex = a, x(E, o), B || O || (B = !0, t(F))), o
				}, e.unstable_shouldYield = function() {
					var t = e.unstable_now();
					M(t);
					var r = T(E);
					return r !== C && null !== C && null !== r && null !== r.callback && r.startTime <= t && r.expirationTime < C.expirationTime || o()
				}, e.unstable_wrapCallback = function(e) {
					var t = A;
					return function() {
						var r = A;
						A = t;
						try {
							return e.apply(this, arguments)
						} finally {
							A = r
						}
					}
				}
			}(w)), w)
		}(_)), y
	}
	/** @license React v16.14.0
	 * react-dom.production.min.js
	 *
	 * Copyright (c) Facebook, Inc. and its affiliates.
	 *
	 * This source code is licensed under the MIT license found in the
	 * LICENSE file in the root directory of this source tree.
	 */
	function x() {
		if (g) return b;
		g = 1;
		var e = u,
			t = d(),
			r = k();

		function n(e) {
			for (var t = "https://reactjs.org/docs/error-decoder.html?invariant=" + e, r = 1; r < arguments.length; r++) t += "&args[]=" + encodeURIComponent(arguments[r]);
			return "Minified React error #" + e + "; visit " + t + " for the full message or use the non-minified dev environment for full errors and additional helpful warnings."
		}
		if (!e) throw Error(n(227));

		function o(e, t, r, n, o, i, a, c, s) {
			var u = Array.prototype.slice.call(arguments, 3);
			try {
				t.apply(r, u)
			} catch (e) {
				this.onError(e)
			}
		}
		var i = !1,
			a = null,
			c = !1,
			s = null,
			l = {
				onError: function(e) {
					i = !0, a = e
				}
			};

		function p(e, t, r, n, c, s, u, d, p) {
			i = !1, a = null, o.apply(l, arguments)
		}
		var f = null,
			m = null,
			h = null;

		function v(e, t, r) {
			var o = e.type || "unknown-event";
			e.currentTarget = h(r),
				function(e, t, r, o, u, l, d, f, m) {
					if (p.apply(this, arguments), i) {
						if (!i) throw Error(n(198));
						var g = a;
						i = !1, a = null, c || (c = !0, s = g)
					}
				}(o, t, void 0, e), e.currentTarget = null
		}
		var y = null,
			_ = {};

		function w() {
			if (y)
				for (var e in _) {
					var t = _[e],
						r = y.indexOf(e);
					if (!(-1 < r)) throw Error(n(96, e));
					if (!T[r]) {
						if (!t.extractEvents) throw Error(n(97, e));
						for (var o in T[r] = t, r = t.eventTypes) {
							var i = void 0,
								a = r[o],
								c = t,
								s = o;
							if (S.hasOwnProperty(s)) throw Error(n(99, s));
							S[s] = a;
							var u = a.phasedRegistrationNames;
							if (u) {
								for (i in u) u.hasOwnProperty(i) && x(u[i], c, s);
								i = !0
							} else a.registrationName ? (x(a.registrationName, c, s), i = !0) : i = !1;
							if (!i) throw Error(n(98, o, e))
						}
					}
				}
		}

		function x(e, t, r) {
			if (I[e]) throw Error(n(100, e));
			I[e] = t, E[e] = t.eventTypes[r].dependencies
		}
		var T = [],
			S = {},
			I = {},
			E = {};

		function P(e) {
			var t, r = !1;
			for (t in e)
				if (e.hasOwnProperty(t)) {
					var o = e[t];
					if (!_.hasOwnProperty(t) || _[t] !== o) {
						if (_[t]) throw Error(n(102, t));
						_[t] = o, r = !0
					}
				} r && w()
		}
		var D = !("undefined" == typeof window || void 0 === window.document || void 0 === window.document.createElement),
			C = null,
			A = null,
			O = null;

		function B(e) {
			if (e = m(e)) {
				if ("function" != typeof C) throw Error(n(280));
				var t = e.stateNode;
				t && (t = f(t), C(e.stateNode, e.type, t))
			}
		}

		function R(e) {
			A ? O ? O.push(e) : O = [e] : A = e
		}

		function M() {
			if (A) {
				var e = A,
					t = O;
				if (O = A = null, B(e), t)
					for (e = 0; e < t.length; e++) B(t[e])
			}
		}

		function z(e, t) {
			return e(t)
		}

		function F(e, t, r, n, o) {
			return e(t, r, n, o)
		}

		function j() {}
		var L = z,
			U = !1,
			N = !1;

		function H() {
			null === A && null === O || (j(), M())
		}

		function V(e, t, r) {
			if (N) return e(t, r);
			N = !0;
			try {
				return L(e, t, r)
			} finally {
				N = !1, H()
			}
		}
		var q = /^[:A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD][:A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD\-.0-9\u00B7\u0300-\u036F\u203F-\u2040]*$/,
			W = Object.prototype.hasOwnProperty,
			G = {},
			K = {};

		function $(e, t, r, n, o, i) {
			this.acceptsBooleans = 2 === t || 3 === t || 4 === t, this.attributeName = n, this.attributeNamespace = o, this.mustUseProperty = r, this.propertyName = e, this.type = t, this.sanitizeURL = i
		}
		var Q = {};
		"children dangerouslySetInnerHTML defaultValue defaultChecked innerHTML suppressContentEditableWarning suppressHydrationWarning style".split(" ").forEach((function(e) {
			Q[e] = new $(e, 0, !1, e, null, !1)
		})), [
			["acceptCharset", "accept-charset"],
			["className", "class"],
			["htmlFor", "for"],
			["httpEquiv", "http-equiv"]
		].forEach((function(e) {
			var t = e[0];
			Q[t] = new $(t, 1, !1, e[1], null, !1)
		})), ["contentEditable", "draggable", "spellCheck", "value"].forEach((function(e) {
			Q[e] = new $(e, 2, !1, e.toLowerCase(), null, !1)
		})), ["autoReverse", "externalResourcesRequired", "focusable", "preserveAlpha"].forEach((function(e) {
			Q[e] = new $(e, 2, !1, e, null, !1)
		})), "allowFullScreen async autoFocus autoPlay controls default defer disabled disablePictureInPicture formNoValidate hidden loop noModule noValidate open playsInline readOnly required reversed scoped seamless itemScope".split(" ").forEach((function(e) {
			Q[e] = new $(e, 3, !1, e.toLowerCase(), null, !1)
		})), ["checked", "multiple", "muted", "selected"].forEach((function(e) {
			Q[e] = new $(e, 3, !0, e, null, !1)
		})), ["capture", "download"].forEach((function(e) {
			Q[e] = new $(e, 4, !1, e, null, !1)
		})), ["cols", "rows", "size", "span"].forEach((function(e) {
			Q[e] = new $(e, 6, !1, e, null, !1)
		})), ["rowSpan", "start"].forEach((function(e) {
			Q[e] = new $(e, 5, !1, e.toLowerCase(), null, !1)
		}));
		var X = /[\-:]([a-z])/g;

		function Y(e) {
			return e[1].toUpperCase()
		}
		"accent-height alignment-baseline arabic-form baseline-shift cap-height clip-path clip-rule color-interpolation color-interpolation-filters color-profile color-rendering dominant-baseline enable-background fill-opacity fill-rule flood-color flood-opacity font-family font-size font-size-adjust font-stretch font-style font-variant font-weight glyph-name glyph-orientation-horizontal glyph-orientation-vertical horiz-adv-x horiz-origin-x image-rendering letter-spacing lighting-color marker-end marker-mid marker-start overline-position overline-thickness paint-order panose-1 pointer-events rendering-intent shape-rendering stop-color stop-opacity strikethrough-position strikethrough-thickness stroke-dasharray stroke-dashoffset stroke-linecap stroke-linejoin stroke-miterlimit stroke-opacity stroke-width text-anchor text-decoration text-rendering underline-position underline-thickness unicode-bidi unicode-range units-per-em v-alphabetic v-hanging v-ideographic v-mathematical vector-effect vert-adv-y vert-origin-x vert-origin-y word-spacing writing-mode xmlns:xlink x-height".split(" ").forEach((function(e) {
			var t = e.replace(X, Y);
			Q[t] = new $(t, 1, !1, e, null, !1)
		})), "xlink:actuate xlink:arcrole xlink:role xlink:show xlink:title xlink:type".split(" ").forEach((function(e) {
			var t = e.replace(X, Y);
			Q[t] = new $(t, 1, !1, e, "http://www.w3.org/1999/xlink", !1)
		})), ["xml:base", "xml:lang", "xml:space"].forEach((function(e) {
			var t = e.replace(X, Y);
			Q[t] = new $(t, 1, !1, e, "http://www.w3.org/XML/1998/namespace", !1)
		})), ["tabIndex", "crossOrigin"].forEach((function(e) {
			Q[e] = new $(e, 1, !1, e.toLowerCase(), null, !1)
		})), Q.xlinkHref = new $("xlinkHref", 1, !1, "xlink:href", "http://www.w3.org/1999/xlink", !0), ["src", "href", "action", "formAction"].forEach((function(e) {
			Q[e] = new $(e, 1, !1, e.toLowerCase(), null, !0)
		}));
		var J = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;

		function Z(e, t, r, n) {
			var o = Q.hasOwnProperty(t) ? Q[t] : null;
			(null !== o ? 0 === o.type : !n && (2 < t.length && ("o" === t[0] || "O" === t[0]) && ("n" === t[1] || "N" === t[1]))) || (function(e, t, r, n) {
				if (null == t || function(e, t, r, n) {
						if (null !== r && 0 === r.type) return !1;
						switch (typeof t) {
							case "function":
							case "symbol":
								return !0;
							case "boolean":
								return !n && (null !== r ? !r.acceptsBooleans : "data-" !== (e = e.toLowerCase().slice(0, 5)) && "aria-" !== e);
							default:
								return !1
						}
					}(e, t, r, n)) return !0;
				if (n) return !1;
				if (null !== r) switch (r.type) {
					case 3:
						return !t;
					case 4:
						return !1 === t;
					case 5:
						return isNaN(t);
					case 6:
						return isNaN(t) || 1 > t
				}
				return !1
			}(t, r, o, n) && (r = null), n || null === o ? function(e) {
				return !!W.call(K, e) || !W.call(G, e) && (q.test(e) ? K[e] = !0 : (G[e] = !0, !1))
			}(t) && (null === r ? e.removeAttribute(t) : e.setAttribute(t, "" + r)) : o.mustUseProperty ? e[o.propertyName] = null === r ? 3 !== o.type && "" : r : (t = o.attributeName, n = o.attributeNamespace, null === r ? e.removeAttribute(t) : (r = 3 === (o = o.type) || 4 === o && !0 === r ? "" : "" + r, n ? e.setAttributeNS(n, t, r) : e.setAttribute(t, r))))
		}
		J.hasOwnProperty("ReactCurrentDispatcher") || (J.ReactCurrentDispatcher = {
			current: null
		}), J.hasOwnProperty("ReactCurrentBatchConfig") || (J.ReactCurrentBatchConfig = {
			suspense: null
		});
		var ee = /^(.*)[\\\/]/,
			te = "function" == typeof Symbol && Symbol.for,
			re = te ? Symbol.for("react.element") : 60103,
			ne = te ? Symbol.for("react.portal") : 60106,
			oe = te ? Symbol.for("react.fragment") : 60107,
			ie = te ? Symbol.for("react.strict_mode") : 60108,
			ae = te ? Symbol.for("react.profiler") : 60114,
			ce = te ? Symbol.for("react.provider") : 60109,
			se = te ? Symbol.for("react.context") : 60110,
			ue = te ? Symbol.for("react.concurrent_mode") : 60111,
			le = te ? Symbol.for("react.forward_ref") : 60112,
			de = te ? Symbol.for("react.suspense") : 60113,
			pe = te ? Symbol.for("react.suspense_list") : 60120,
			fe = te ? Symbol.for("react.memo") : 60115,
			me = te ? Symbol.for("react.lazy") : 60116,
			ge = te ? Symbol.for("react.block") : 60121,
			he = "function" == typeof Symbol && Symbol.iterator;

		function ve(e) {
			return null === e || "object" != typeof e ? null : "function" == typeof(e = he && e[he] || e["@@iterator"]) ? e : null
		}

		function be(e) {
			if (null == e) return null;
			if ("function" == typeof e) return e.displayName || e.name || null;
			if ("string" == typeof e) return e;
			switch (e) {
				case oe:
					return "Fragment";
				case ne:
					return "Portal";
				case ae:
					return "Profiler";
				case ie:
					return "StrictMode";
				case de:
					return "Suspense";
				case pe:
					return "SuspenseList"
			}
			if ("object" == typeof e) switch (e.$$typeof) {
				case se:
					return "Context.Consumer";
				case ce:
					return "Context.Provider";
				case le:
					var t = e.render;
					return t = t.displayName || t.name || "", e.displayName || ("" !== t ? "ForwardRef(" + t + ")" : "ForwardRef");
				case fe:
					return be(e.type);
				case ge:
					return be(e.render);
				case me:
					if (e = 1 === e._status ? e._result : null) return be(e)
			}
			return null
		}

		function ye(e) {
			var t = "";
			do {
				e: switch (e.tag) {
					case 3:
					case 4:
					case 6:
					case 7:
					case 10:
					case 9:
						var r = "";
						break e;
					default:
						var n = e._debugOwner,
							o = e._debugSource,
							i = be(e.type);
						r = null, n && (r = be(n.type)), n = i, i = "", o ? i = " (at " + o.fileName.replace(ee, "") + ":" + o.lineNumber + ")" : r && (i = " (created by " + r + ")"), r = "\n    in " + (n || "Unknown") + i
				}
				t += r,
				e = e.return
			} while (e);
			return t
		}

		function _e(e) {
			switch (typeof e) {
				case "boolean":
				case "number":
				case "object":
				case "string":
				case "undefined":
					return e;
				default:
					return ""
			}
		}

		function we(e) {
			var t = e.type;
			return (e = e.nodeName) && "input" === e.toLowerCase() && ("checkbox" === t || "radio" === t)
		}

		function ke(e) {
			e._valueTracker || (e._valueTracker = function(e) {
				var t = we(e) ? "checked" : "value",
					r = Object.getOwnPropertyDescriptor(e.constructor.prototype, t),
					n = "" + e[t];
				if (!e.hasOwnProperty(t) && void 0 !== r && "function" == typeof r.get && "function" == typeof r.set) {
					var o = r.get,
						i = r.set;
					return Object.defineProperty(e, t, {
						configurable: !0,
						get: function() {
							return o.call(this)
						},
						set: function(e) {
							n = "" + e, i.call(this, e)
						}
					}), Object.defineProperty(e, t, {
						enumerable: r.enumerable
					}), {
						getValue: function() {
							return n
						},
						setValue: function(e) {
							n = "" + e
						},
						stopTracking: function() {
							e._valueTracker = null, delete e[t]
						}
					}
				}
			}(e))
		}

		function xe(e) {
			if (!e) return !1;
			var t = e._valueTracker;
			if (!t) return !0;
			var r = t.getValue(),
				n = "";
			return e && (n = we(e) ? e.checked ? "true" : "false" : e.value), (e = n) !== r && (t.setValue(e), !0)
		}

		function Te(e, r) {
			var n = r.checked;
			return t({}, r, {
				defaultChecked: void 0,
				defaultValue: void 0,
				value: void 0,
				checked: null != n ? n : e._wrapperState.initialChecked
			})
		}

		function Se(e, t) {
			var r = null == t.defaultValue ? "" : t.defaultValue,
				n = null != t.checked ? t.checked : t.defaultChecked;
			r = _e(null != t.value ? t.value : r), e._wrapperState = {
				initialChecked: n,
				initialValue: r,
				controlled: "checkbox" === t.type || "radio" === t.type ? null != t.checked : null != t.value
			}
		}

		function Ie(e, t) {
			null != (t = t.checked) && Z(e, "checked", t, !1)
		}

		function Ee(e, t) {
			Ie(e, t);
			var r = _e(t.value),
				n = t.type;
			if (null != r) "number" === n ? (0 === r && "" === e.value || e.value != r) && (e.value = "" + r) : e.value !== "" + r && (e.value = "" + r);
			else if ("submit" === n || "reset" === n) return void e.removeAttribute("value");
			t.hasOwnProperty("value") ? De(e, t.type, r) : t.hasOwnProperty("defaultValue") && De(e, t.type, _e(t.defaultValue)), null == t.checked && null != t.defaultChecked && (e.defaultChecked = !!t.defaultChecked)
		}

		function Pe(e, t, r) {
			if (t.hasOwnProperty("value") || t.hasOwnProperty("defaultValue")) {
				var n = t.type;
				if (!("submit" !== n && "reset" !== n || void 0 !== t.value && null !== t.value)) return;
				t = "" + e._wrapperState.initialValue, r || t === e.value || (e.value = t), e.defaultValue = t
			}
			"" !== (r = e.name) && (e.name = ""), e.defaultChecked = !!e._wrapperState.initialChecked, "" !== r && (e.name = r)
		}

		function De(e, t, r) {
			"number" === t && e.ownerDocument.activeElement === e || (null == r ? e.defaultValue = "" + e._wrapperState.initialValue : e.defaultValue !== "" + r && (e.defaultValue = "" + r))
		}

		function Ce(r, n) {
			return r = t({
				children: void 0
			}, n), (n = function(t) {
				var r = "";
				return e.Children.forEach(t, (function(e) {
					null != e && (r += e)
				})), r
			}(n.children)) && (r.children = n), r
		}

		function Ae(e, t, r, n) {
			if (e = e.options, t) {
				t = {};
				for (var o = 0; o < r.length; o++) t["$" + r[o]] = !0;
				for (r = 0; r < e.length; r++) o = t.hasOwnProperty("$" + e[r].value), e[r].selected !== o && (e[r].selected = o), o && n && (e[r].defaultSelected = !0)
			} else {
				for (r = "" + _e(r), t = null, o = 0; o < e.length; o++) {
					if (e[o].value === r) return e[o].selected = !0, void(n && (e[o].defaultSelected = !0));
					null !== t || e[o].disabled || (t = e[o])
				}
				null !== t && (t.selected = !0)
			}
		}

		function Oe(e, r) {
			if (null != r.dangerouslySetInnerHTML) throw Error(n(91));
			return t({}, r, {
				value: void 0,
				defaultValue: void 0,
				children: "" + e._wrapperState.initialValue
			})
		}

		function Be(e, t) {
			var r = t.value;
			if (null == r) {
				if (r = t.children, t = t.defaultValue, null != r) {
					if (null != t) throw Error(n(92));
					if (Array.isArray(r)) {
						if (!(1 >= r.length)) throw Error(n(93));
						r = r[0]
					}
					t = r
				}
				null == t && (t = ""), r = t
			}
			e._wrapperState = {
				initialValue: _e(r)
			}
		}

		function Re(e, t) {
			var r = _e(t.value),
				n = _e(t.defaultValue);
			null != r && ((r = "" + r) !== e.value && (e.value = r), null == t.defaultValue && e.defaultValue !== r && (e.defaultValue = r)), null != n && (e.defaultValue = "" + n)
		}

		function Me(e) {
			var t = e.textContent;
			t === e._wrapperState.initialValue && "" !== t && null !== t && (e.value = t)
		}
		var ze = "http://www.w3.org/1999/xhtml",
			Fe = "http://www.w3.org/2000/svg";

		function je(e) {
			switch (e) {
				case "svg":
					return "http://www.w3.org/2000/svg";
				case "math":
					return "http://www.w3.org/1998/Math/MathML";
				default:
					return "http://www.w3.org/1999/xhtml"
			}
		}

		function Le(e, t) {
			return null == e || "http://www.w3.org/1999/xhtml" === e ? je(t) : "http://www.w3.org/2000/svg" === e && "foreignObject" === t ? "http://www.w3.org/1999/xhtml" : e
		}
		var Ue, Ne, He = (Ne = function(e, t) {
			if (e.namespaceURI !== Fe || "innerHTML" in e) e.innerHTML = t;
			else {
				for ((Ue = Ue || document.createElement("div")).innerHTML = "<svg>" + t.valueOf().toString() + "</svg>", t = Ue.firstChild; e.firstChild;) e.removeChild(e.firstChild);
				for (; t.firstChild;) e.appendChild(t.firstChild)
			}
		}, "undefined" != typeof MSApp && MSApp.execUnsafeLocalFunction ? function(e, t, r, n) {
			MSApp.execUnsafeLocalFunction((function() {
				return Ne(e, t)
			}))
		} : Ne);

		function Ve(e, t) {
			if (t) {
				var r = e.firstChild;
				if (r && r === e.lastChild && 3 === r.nodeType) return void(r.nodeValue = t)
			}
			e.textContent = t
		}

		function qe(e, t) {
			var r = {};
			return r[e.toLowerCase()] = t.toLowerCase(), r["Webkit" + e] = "webkit" + t, r["Moz" + e] = "moz" + t, r
		}
		var We = {
				animationend: qe("Animation", "AnimationEnd"),
				animationiteration: qe("Animation", "AnimationIteration"),
				animationstart: qe("Animation", "AnimationStart"),
				transitionend: qe("Transition", "TransitionEnd")
			},
			Ge = {},
			Ke = {};

		function $e(e) {
			if (Ge[e]) return Ge[e];
			if (!We[e]) return e;
			var t, r = We[e];
			for (t in r)
				if (r.hasOwnProperty(t) && t in Ke) return Ge[e] = r[t];
			return e
		}
		D && (Ke = document.createElement("div").style, "AnimationEvent" in window || (delete We.animationend.animation, delete We.animationiteration.animation, delete We.animationstart.animation), "TransitionEvent" in window || delete We.transitionend.transition);
		var Qe = $e("animationend"),
			Xe = $e("animationiteration"),
			Ye = $e("animationstart"),
			Je = $e("transitionend"),
			Ze = "abort canplay canplaythrough durationchange emptied encrypted ended error loadeddata loadedmetadata loadstart pause play playing progress ratechange seeked seeking stalled suspend timeupdate volumechange waiting".split(" "),
			et = new("function" == typeof WeakMap ? WeakMap : Map);

		function tt(e) {
			var t = et.get(e);
			return void 0 === t && (t = new Map, et.set(e, t)), t
		}

		function rt(e) {
			var t = e,
				r = e;
			if (e.alternate)
				for (; t.return;) t = t.return;
			else {
				e = t;
				do {
					0 != (1026 & (t = e).effectTag) && (r = t.return), e = t.return
				} while (e)
			}
			return 3 === t.tag ? r : null
		}

		function nt(e) {
			if (13 === e.tag) {
				var t = e.memoizedState;
				if (null === t && (null !== (e = e.alternate) && (t = e.memoizedState)), null !== t) return t.dehydrated
			}
			return null
		}

		function ot(e) {
			if (rt(e) !== e) throw Error(n(188))
		}

		function it(e) {
			if (e = function(e) {
					var t = e.alternate;
					if (!t) {
						if (null === (t = rt(e))) throw Error(n(188));
						return t !== e ? null : e
					}
					for (var r = e, o = t;;) {
						var i = r.return;
						if (null === i) break;
						var a = i.alternate;
						if (null === a) {
							if (null !== (o = i.return)) {
								r = o;
								continue
							}
							break
						}
						if (i.child === a.child) {
							for (a = i.child; a;) {
								if (a === r) return ot(i), e;
								if (a === o) return ot(i), t;
								a = a.sibling
							}
							throw Error(n(188))
						}
						if (r.return !== o.return) r = i, o = a;
						else {
							for (var c = !1, s = i.child; s;) {
								if (s === r) {
									c = !0, r = i, o = a;
									break
								}
								if (s === o) {
									c = !0, o = i, r = a;
									break
								}
								s = s.sibling
							}
							if (!c) {
								for (s = a.child; s;) {
									if (s === r) {
										c = !0, r = a, o = i;
										break
									}
									if (s === o) {
										c = !0, o = a, r = i;
										break
									}
									s = s.sibling
								}
								if (!c) throw Error(n(189))
							}
						}
						if (r.alternate !== o) throw Error(n(190))
					}
					if (3 !== r.tag) throw Error(n(188));
					return r.stateNode.current === r ? e : t
				}(e), !e) return null;
			for (var t = e;;) {
				if (5 === t.tag || 6 === t.tag) return t;
				if (t.child) t.child.return = t, t = t.child;
				else {
					if (t === e) break;
					for (; !t.sibling;) {
						if (!t.return || t.return === e) return null;
						t = t.return
					}
					t.sibling.return = t.return, t = t.sibling
				}
			}
			return null
		}

		function at(e, t) {
			if (null == t) throw Error(n(30));
			return null == e ? t : Array.isArray(e) ? Array.isArray(t) ? (e.push.apply(e, t), e) : (e.push(t), e) : Array.isArray(t) ? [e].concat(t) : [e, t]
		}

		function ct(e, t, r) {
			Array.isArray(e) ? e.forEach(t, r) : e && t.call(r, e)
		}
		var st = null;

		function ut(e) {
			if (e) {
				var t = e._dispatchListeners,
					r = e._dispatchInstances;
				if (Array.isArray(t))
					for (var n = 0; n < t.length && !e.isPropagationStopped(); n++) v(e, t[n], r[n]);
				else t && v(e, t, r);
				e._dispatchListeners = null, e._dispatchInstances = null, e.isPersistent() || e.constructor.release(e)
			}
		}

		function lt(e) {
			if (null !== e && (st = at(st, e)), e = st, st = null, e) {
				if (ct(e, ut), st) throw Error(n(95));
				if (c) throw e = s, c = !1, s = null, e
			}
		}

		function dt(e) {
			return (e = e.target || e.srcElement || window).correspondingUseElement && (e = e.correspondingUseElement), 3 === e.nodeType ? e.parentNode : e
		}

		function pt(e) {
			if (!D) return !1;
			var t = (e = "on" + e) in document;
			return t || ((t = document.createElement("div")).setAttribute(e, "return;"), t = "function" == typeof t[e]), t
		}
		var ft = [];

		function mt(e) {
			e.topLevelType = null, e.nativeEvent = null, e.targetInst = null, e.ancestors.length = 0, 10 > ft.length && ft.push(e)
		}

		function gt(e, t, r, n) {
			if (ft.length) {
				var o = ft.pop();
				return o.topLevelType = e, o.eventSystemFlags = n, o.nativeEvent = t, o.targetInst = r, o
			}
			return {
				topLevelType: e,
				eventSystemFlags: n,
				nativeEvent: t,
				targetInst: r,
				ancestors: []
			}
		}

		function ht(e) {
			var t = e.targetInst,
				r = t;
			do {
				if (!r) {
					e.ancestors.push(r);
					break
				}
				var n = r;
				if (3 === n.tag) n = n.stateNode.containerInfo;
				else {
					for (; n.return;) n = n.return;
					n = 3 !== n.tag ? null : n.stateNode.containerInfo
				}
				if (!n) break;
				5 !== (t = r.tag) && 6 !== t || e.ancestors.push(r), r = Rr(n)
			} while (r);
			for (r = 0; r < e.ancestors.length; r++) {
				t = e.ancestors[r];
				var o = dt(e.nativeEvent);
				n = e.topLevelType;
				var i = e.nativeEvent,
					a = e.eventSystemFlags;
				0 === r && (a |= 64);
				for (var c = null, s = 0; s < T.length; s++) {
					var u = T[s];
					u && (u = u.extractEvents(n, t, i, o, a)) && (c = at(c, u))
				}
				lt(c)
			}
		}

		function vt(e, t, r) {
			if (!r.has(e)) {
				switch (e) {
					case "scroll":
						Yt(t, "scroll", !0);
						break;
					case "focus":
					case "blur":
						Yt(t, "focus", !0), Yt(t, "blur", !0), r.set("blur", null), r.set("focus", null);
						break;
					case "cancel":
					case "close":
						pt(e) && Yt(t, e, !0);
						break;
					case "invalid":
					case "submit":
					case "reset":
						break;
					default:
						-1 === Ze.indexOf(e) && Xt(e, t)
				}
				r.set(e, null)
			}
		}
		var bt, yt, _t, wt = !1,
			kt = [],
			xt = null,
			Tt = null,
			St = null,
			It = new Map,
			Et = new Map,
			Pt = [],
			Dt = "mousedown mouseup touchcancel touchend touchstart auxclick dblclick pointercancel pointerdown pointerup dragend dragstart drop compositionend compositionstart keydown keypress keyup input textInput close cancel copy cut paste click change contextmenu reset submit".split(" "),
			Ct = "focus blur dragenter dragleave mouseover mouseout pointerover pointerout gotpointercapture lostpointercapture".split(" ");

		function At(e, t, r, n, o) {
			return {
				blockedOn: e,
				topLevelType: t,
				eventSystemFlags: 32 | r,
				nativeEvent: o,
				container: n
			}
		}

		function Ot(e, t) {
			switch (e) {
				case "focus":
				case "blur":
					xt = null;
					break;
				case "dragenter":
				case "dragleave":
					Tt = null;
					break;
				case "mouseover":
				case "mouseout":
					St = null;
					break;
				case "pointerover":
				case "pointerout":
					It.delete(t.pointerId);
					break;
				case "gotpointercapture":
				case "lostpointercapture":
					Et.delete(t.pointerId)
			}
		}

		function Bt(e, t, r, n, o, i) {
			return null === e || e.nativeEvent !== i ? (e = At(t, r, n, o, i), null !== t && (null !== (t = Mr(t)) && yt(t)), e) : (e.eventSystemFlags |= n, e)
		}

		function Rt(e) {
			var t = Rr(e.target);
			if (null !== t) {
				var n = rt(t);
				if (null !== n)
					if (13 === (t = n.tag)) {
						if (null !== (t = nt(n))) return e.blockedOn = t, void r.unstable_runWithPriority(e.priority, (function() {
							_t(n)
						}))
					} else if (3 === t && n.stateNode.hydrate) return void(e.blockedOn = 3 === n.tag ? n.stateNode.containerInfo : null)
			}
			e.blockedOn = null
		}

		function Mt(e) {
			if (null !== e.blockedOn) return !1;
			var t = tr(e.topLevelType, e.eventSystemFlags, e.container, e.nativeEvent);
			if (null !== t) {
				var r = Mr(t);
				return null !== r && yt(r), e.blockedOn = t, !1
			}
			return !0
		}

		function zt(e, t, r) {
			Mt(e) && r.delete(t)
		}

		function Ft() {
			for (wt = !1; 0 < kt.length;) {
				var e = kt[0];
				if (null !== e.blockedOn) {
					null !== (e = Mr(e.blockedOn)) && bt(e);
					break
				}
				var t = tr(e.topLevelType, e.eventSystemFlags, e.container, e.nativeEvent);
				null !== t ? e.blockedOn = t : kt.shift()
			}
			null !== xt && Mt(xt) && (xt = null), null !== Tt && Mt(Tt) && (Tt = null), null !== St && Mt(St) && (St = null), It.forEach(zt), Et.forEach(zt)
		}

		function jt(e, t) {
			e.blockedOn === t && (e.blockedOn = null, wt || (wt = !0, r.unstable_scheduleCallback(r.unstable_NormalPriority, Ft)))
		}

		function Lt(e) {
			function t(t) {
				return jt(t, e)
			}
			if (0 < kt.length) {
				jt(kt[0], e);
				for (var r = 1; r < kt.length; r++) {
					var n = kt[r];
					n.blockedOn === e && (n.blockedOn = null)
				}
			}
			for (null !== xt && jt(xt, e), null !== Tt && jt(Tt, e), null !== St && jt(St, e), It.forEach(t), Et.forEach(t), r = 0; r < Pt.length; r++)(n = Pt[r]).blockedOn === e && (n.blockedOn = null);
			for (; 0 < Pt.length && null === (r = Pt[0]).blockedOn;) Rt(r), null === r.blockedOn && Pt.shift()
		}
		var Ut = {},
			Nt = new Map,
			Ht = new Map,
			Vt = ["abort", "abort", Qe, "animationEnd", Xe, "animationIteration", Ye, "animationStart", "canplay", "canPlay", "canplaythrough", "canPlayThrough", "durationchange", "durationChange", "emptied", "emptied", "encrypted", "encrypted", "ended", "ended", "error", "error", "gotpointercapture", "gotPointerCapture", "load", "load", "loadeddata", "loadedData", "loadedmetadata", "loadedMetadata", "loadstart", "loadStart", "lostpointercapture", "lostPointerCapture", "playing", "playing", "progress", "progress", "seeking", "seeking", "stalled", "stalled", "suspend", "suspend", "timeupdate", "timeUpdate", Je, "transitionEnd", "waiting", "waiting"];

		function qt(e, t) {
			for (var r = 0; r < e.length; r += 2) {
				var n = e[r],
					o = e[r + 1],
					i = "on" + (o[0].toUpperCase() + o.slice(1));
				i = {
					phasedRegistrationNames: {
						bubbled: i,
						captured: i + "Capture"
					},
					dependencies: [n],
					eventPriority: t
				}, Ht.set(n, t), Nt.set(n, i), Ut[o] = i
			}
		}
		qt("blur blur cancel cancel click click close close contextmenu contextMenu copy copy cut cut auxclick auxClick dblclick doubleClick dragend dragEnd dragstart dragStart drop drop focus focus input input invalid invalid keydown keyDown keypress keyPress keyup keyUp mousedown mouseDown mouseup mouseUp paste paste pause pause play play pointercancel pointerCancel pointerdown pointerDown pointerup pointerUp ratechange rateChange reset reset seeked seeked submit submit touchcancel touchCancel touchend touchEnd touchstart touchStart volumechange volumeChange".split(" "), 0), qt("drag drag dragenter dragEnter dragexit dragExit dragleave dragLeave dragover dragOver mousemove mouseMove mouseout mouseOut mouseover mouseOver pointermove pointerMove pointerout pointerOut pointerover pointerOver scroll scroll toggle toggle touchmove touchMove wheel wheel".split(" "), 1), qt(Vt, 2);
		for (var Wt = "change selectionchange textInput compositionstart compositionend compositionupdate".split(" "), Gt = 0; Gt < Wt.length; Gt++) Ht.set(Wt[Gt], 0);
		var Kt = r.unstable_UserBlockingPriority,
			$t = r.unstable_runWithPriority,
			Qt = !0;

		function Xt(e, t) {
			Yt(t, e, !1)
		}

		function Yt(e, t, r) {
			var n = Ht.get(t);
			switch (void 0 === n ? 2 : n) {
				case 0:
					n = Jt.bind(null, t, 1, e);
					break;
				case 1:
					n = Zt.bind(null, t, 1, e);
					break;
				default:
					n = er.bind(null, t, 1, e)
			}
			r ? e.addEventListener(t, n, !0) : e.addEventListener(t, n, !1)
		}

		function Jt(e, t, r, n) {
			U || j();
			var o = er,
				i = U;
			U = !0;
			try {
				F(o, e, t, r, n)
			} finally {
				(U = i) || H()
			}
		}

		function Zt(e, t, r, n) {
			$t(Kt, er.bind(null, e, t, r, n))
		}

		function er(e, t, r, n) {
			if (Qt)
				if (0 < kt.length && -1 < Dt.indexOf(e)) e = At(null, e, t, r, n), kt.push(e);
				else {
					var o = tr(e, t, r, n);
					if (null === o) Ot(e, n);
					else if (-1 < Dt.indexOf(e)) e = At(o, e, t, r, n), kt.push(e);
					else if (! function(e, t, r, n, o) {
							switch (t) {
								case "focus":
									return xt = Bt(xt, e, t, r, n, o), !0;
								case "dragenter":
									return Tt = Bt(Tt, e, t, r, n, o), !0;
								case "mouseover":
									return St = Bt(St, e, t, r, n, o), !0;
								case "pointerover":
									var i = o.pointerId;
									return It.set(i, Bt(It.get(i) || null, e, t, r, n, o)), !0;
								case "gotpointercapture":
									return i = o.pointerId, Et.set(i, Bt(Et.get(i) || null, e, t, r, n, o)), !0
							}
							return !1
						}(o, e, t, r, n)) {
						Ot(e, n), e = gt(e, n, null, t);
						try {
							V(ht, e)
						} finally {
							mt(e)
						}
					}
				}
		}

		function tr(e, t, r, n) {
			if (null !== (r = Rr(r = dt(n)))) {
				var o = rt(r);
				if (null === o) r = null;
				else {
					var i = o.tag;
					if (13 === i) {
						if (null !== (r = nt(o))) return r;
						r = null
					} else if (3 === i) {
						if (o.stateNode.hydrate) return 3 === o.tag ? o.stateNode.containerInfo : null;
						r = null
					} else o !== r && (r = null)
				}
			}
			e = gt(e, n, r, t);
			try {
				V(ht, e)
			} finally {
				mt(e)
			}
			return null
		}
		var rr = {
				animationIterationCount: !0,
				borderImageOutset: !0,
				borderImageSlice: !0,
				borderImageWidth: !0,
				boxFlex: !0,
				boxFlexGroup: !0,
				boxOrdinalGroup: !0,
				columnCount: !0,
				columns: !0,
				flex: !0,
				flexGrow: !0,
				flexPositive: !0,
				flexShrink: !0,
				flexNegative: !0,
				flexOrder: !0,
				gridArea: !0,
				gridRow: !0,
				gridRowEnd: !0,
				gridRowSpan: !0,
				gridRowStart: !0,
				gridColumn: !0,
				gridColumnEnd: !0,
				gridColumnSpan: !0,
				gridColumnStart: !0,
				fontWeight: !0,
				lineClamp: !0,
				lineHeight: !0,
				opacity: !0,
				order: !0,
				orphans: !0,
				tabSize: !0,
				widows: !0,
				zIndex: !0,
				zoom: !0,
				fillOpacity: !0,
				floodOpacity: !0,
				stopOpacity: !0,
				strokeDasharray: !0,
				strokeDashoffset: !0,
				strokeMiterlimit: !0,
				strokeOpacity: !0,
				strokeWidth: !0
			},
			nr = ["Webkit", "ms", "Moz", "O"];

		function or(e, t, r) {
			return null == t || "boolean" == typeof t || "" === t ? "" : r || "number" != typeof t || 0 === t || rr.hasOwnProperty(e) && rr[e] ? ("" + t).trim() : t + "px"
		}

		function ir(e, t) {
			for (var r in e = e.style, t)
				if (t.hasOwnProperty(r)) {
					var n = 0 === r.indexOf("--"),
						o = or(r, t[r], n);
					"float" === r && (r = "cssFloat"), n ? e.setProperty(r, o) : e[r] = o
				}
		}
		Object.keys(rr).forEach((function(e) {
			nr.forEach((function(t) {
				t = t + e.charAt(0).toUpperCase() + e.substring(1), rr[t] = rr[e]
			}))
		}));
		var ar = t({
			menuitem: !0
		}, {
			area: !0,
			base: !0,
			br: !0,
			col: !0,
			embed: !0,
			hr: !0,
			img: !0,
			input: !0,
			keygen: !0,
			link: !0,
			meta: !0,
			param: !0,
			source: !0,
			track: !0,
			wbr: !0
		});

		function cr(e, t) {
			if (t) {
				if (ar[e] && (null != t.children || null != t.dangerouslySetInnerHTML)) throw Error(n(137, e, ""));
				if (null != t.dangerouslySetInnerHTML) {
					if (null != t.children) throw Error(n(60));
					if ("object" != typeof t.dangerouslySetInnerHTML || !("__html" in t.dangerouslySetInnerHTML)) throw Error(n(61))
				}
				if (null != t.style && "object" != typeof t.style) throw Error(n(62, ""))
			}
		}

		function sr(e, t) {
			if (-1 === e.indexOf("-")) return "string" == typeof t.is;
			switch (e) {
				case "annotation-xml":
				case "color-profile":
				case "font-face":
				case "font-face-src":
				case "font-face-uri":
				case "font-face-format":
				case "font-face-name":
				case "missing-glyph":
					return !1;
				default:
					return !0
			}
		}
		var ur = ze;

		function lr(e, t) {
			var r = tt(e = 9 === e.nodeType || 11 === e.nodeType ? e : e.ownerDocument);
			t = E[t];
			for (var n = 0; n < t.length; n++) vt(t[n], e, r)
		}

		function dr() {}

		function pr(e) {
			if (void 0 === (e = e || ("undefined" != typeof document ? document : void 0))) return null;
			try {
				return e.activeElement || e.body
			} catch (t) {
				return e.body
			}
		}

		function fr(e) {
			for (; e && e.firstChild;) e = e.firstChild;
			return e
		}

		function mr(e, t) {
			var r, n = fr(e);
			for (e = 0; n;) {
				if (3 === n.nodeType) {
					if (r = e + n.textContent.length, e <= t && r >= t) return {
						node: n,
						offset: t - e
					};
					e = r
				}
				e: {
					for (; n;) {
						if (n.nextSibling) {
							n = n.nextSibling;
							break e
						}
						n = n.parentNode
					}
					n = void 0
				}
				n = fr(n)
			}
		}

		function gr(e, t) {
			return !(!e || !t) && (e === t || (!e || 3 !== e.nodeType) && (t && 3 === t.nodeType ? gr(e, t.parentNode) : "contains" in e ? e.contains(t) : !!e.compareDocumentPosition && !!(16 & e.compareDocumentPosition(t))))
		}

		function hr() {
			for (var e = window, t = pr(); t instanceof e.HTMLIFrameElement;) {
				try {
					var r = "string" == typeof t.contentWindow.location.href
				} catch (e) {
					r = !1
				}
				if (!r) break;
				t = pr((e = t.contentWindow).document)
			}
			return t
		}

		function vr(e) {
			var t = e && e.nodeName && e.nodeName.toLowerCase();
			return t && ("input" === t && ("text" === e.type || "search" === e.type || "tel" === e.type || "url" === e.type || "password" === e.type) || "textarea" === t || "true" === e.contentEditable)
		}
		var br = "$",
			yr = "/$",
			_r = "$?",
			wr = "$!",
			kr = null,
			xr = null;

		function Tr(e, t) {
			switch (e) {
				case "button":
				case "input":
				case "select":
				case "textarea":
					return !!t.autoFocus
			}
			return !1
		}

		function Sr(e, t) {
			return "textarea" === e || "option" === e || "noscript" === e || "string" == typeof t.children || "number" == typeof t.children || "object" == typeof t.dangerouslySetInnerHTML && null !== t.dangerouslySetInnerHTML && null != t.dangerouslySetInnerHTML.__html
		}
		var Ir = "function" == typeof setTimeout ? setTimeout : void 0,
			Er = "function" == typeof clearTimeout ? clearTimeout : void 0;

		function Pr(e) {
			for (; null != e; e = e.nextSibling) {
				var t = e.nodeType;
				if (1 === t || 3 === t) break
			}
			return e
		}

		function Dr(e) {
			e = e.previousSibling;
			for (var t = 0; e;) {
				if (8 === e.nodeType) {
					var r = e.data;
					if (r === br || r === wr || r === _r) {
						if (0 === t) return e;
						t--
					} else r === yr && t++
				}
				e = e.previousSibling
			}
			return null
		}
		var Cr = Math.random().toString(36).slice(2),
			Ar = "__reactInternalInstance$" + Cr,
			Or = "__reactEventHandlers$" + Cr,
			Br = "__reactContainere$" + Cr;

		function Rr(e) {
			var t = e[Ar];
			if (t) return t;
			for (var r = e.parentNode; r;) {
				if (t = r[Br] || r[Ar]) {
					if (r = t.alternate, null !== t.child || null !== r && null !== r.child)
						for (e = Dr(e); null !== e;) {
							if (r = e[Ar]) return r;
							e = Dr(e)
						}
					return t
				}
				r = (e = r).parentNode
			}
			return null
		}

		function Mr(e) {
			return !(e = e[Ar] || e[Br]) || 5 !== e.tag && 6 !== e.tag && 13 !== e.tag && 3 !== e.tag ? null : e
		}

		function zr(e) {
			if (5 === e.tag || 6 === e.tag) return e.stateNode;
			throw Error(n(33))
		}

		function Fr(e) {
			return e[Or] || null
		}

		function jr(e) {
			do {
				e = e.return
			} while (e && 5 !== e.tag);
			return e || null
		}

		function Lr(e, t) {
			var r = e.stateNode;
			if (!r) return null;
			var o = f(r);
			if (!o) return null;
			r = o[t];
			e: switch (t) {
				case "onClick":
				case "onClickCapture":
				case "onDoubleClick":
				case "onDoubleClickCapture":
				case "onMouseDown":
				case "onMouseDownCapture":
				case "onMouseMove":
				case "onMouseMoveCapture":
				case "onMouseUp":
				case "onMouseUpCapture":
				case "onMouseEnter":
					(o = !o.disabled) || (o = !("button" === (e = e.type) || "input" === e || "select" === e || "textarea" === e)), e = !o;
					break e;
				default:
					e = !1
			}
			if (e) return null;
			if (r && "function" != typeof r) throw Error(n(231, t, typeof r));
			return r
		}

		function Ur(e, t, r) {
			(t = Lr(e, r.dispatchConfig.phasedRegistrationNames[t])) && (r._dispatchListeners = at(r._dispatchListeners, t), r._dispatchInstances = at(r._dispatchInstances, e))
		}

		function Nr(e) {
			if (e && e.dispatchConfig.phasedRegistrationNames) {
				for (var t = e._targetInst, r = []; t;) r.push(t), t = jr(t);
				for (t = r.length; 0 < t--;) Ur(r[t], "captured", e);
				for (t = 0; t < r.length; t++) Ur(r[t], "bubbled", e)
			}
		}

		function Hr(e, t, r) {
			e && r && r.dispatchConfig.registrationName && (t = Lr(e, r.dispatchConfig.registrationName)) && (r._dispatchListeners = at(r._dispatchListeners, t), r._dispatchInstances = at(r._dispatchInstances, e))
		}

		function Vr(e) {
			e && e.dispatchConfig.registrationName && Hr(e._targetInst, null, e)
		}

		function qr(e) {
			ct(e, Nr)
		}
		var Wr = null,
			Gr = null,
			Kr = null;

		function $r() {
			if (Kr) return Kr;
			var e, t, r = Gr,
				n = r.length,
				o = "value" in Wr ? Wr.value : Wr.textContent,
				i = o.length;
			for (e = 0; e < n && r[e] === o[e]; e++);
			var a = n - e;
			for (t = 1; t <= a && r[n - t] === o[i - t]; t++);
			return Kr = o.slice(e, 1 < t ? 1 - t : void 0)
		}

		function Qr() {
			return !0
		}

		function Xr() {
			return !1
		}

		function Yr(e, t, r, n) {
			for (var o in this.dispatchConfig = e, this._targetInst = t, this.nativeEvent = r, e = this.constructor.Interface) e.hasOwnProperty(o) && ((t = e[o]) ? this[o] = t(r) : "target" === o ? this.target = n : this[o] = r[o]);
			return this.isDefaultPrevented = (null != r.defaultPrevented ? r.defaultPrevented : !1 === r.returnValue) ? Qr : Xr, this.isPropagationStopped = Xr, this
		}

		function Jr(e, t, r, n) {
			if (this.eventPool.length) {
				var o = this.eventPool.pop();
				return this.call(o, e, t, r, n), o
			}
			return new this(e, t, r, n)
		}

		function Zr(e) {
			if (!(e instanceof this)) throw Error(n(279));
			e.destructor(), 10 > this.eventPool.length && this.eventPool.push(e)
		}

		function en(e) {
			e.eventPool = [], e.getPooled = Jr, e.release = Zr
		}
		t(Yr.prototype, {
			preventDefault: function() {
				this.defaultPrevented = !0;
				var e = this.nativeEvent;
				e && (e.preventDefault ? e.preventDefault() : "unknown" != typeof e.returnValue && (e.returnValue = !1), this.isDefaultPrevented = Qr)
			},
			stopPropagation: function() {
				var e = this.nativeEvent;
				e && (e.stopPropagation ? e.stopPropagation() : "unknown" != typeof e.cancelBubble && (e.cancelBubble = !0), this.isPropagationStopped = Qr)
			},
			persist: function() {
				this.isPersistent = Qr
			},
			isPersistent: Xr,
			destructor: function() {
				var e, t = this.constructor.Interface;
				for (e in t) this[e] = null;
				this.nativeEvent = this._targetInst = this.dispatchConfig = null, this.isPropagationStopped = this.isDefaultPrevented = Xr, this._dispatchInstances = this._dispatchListeners = null
			}
		}), Yr.Interface = {
			type: null,
			target: null,
			currentTarget: function() {
				return null
			},
			eventPhase: null,
			bubbles: null,
			cancelable: null,
			timeStamp: function(e) {
				return e.timeStamp || Date.now()
			},
			defaultPrevented: null,
			isTrusted: null
		}, Yr.extend = function(e) {
			function r() {}

			function n() {
				return o.apply(this, arguments)
			}
			var o = this;
			r.prototype = o.prototype;
			var i = new r;
			return t(i, n.prototype), n.prototype = i, n.prototype.constructor = n, n.Interface = t({}, o.Interface, e), n.extend = o.extend, en(n), n
		}, en(Yr);
		var tn = Yr.extend({
				data: null
			}),
			rn = Yr.extend({
				data: null
			}),
			nn = [9, 13, 27, 32],
			on = D && "CompositionEvent" in window,
			an = null;
		D && "documentMode" in document && (an = document.documentMode);
		var cn = D && "TextEvent" in window && !an,
			sn = D && (!on || an && 8 < an && 11 >= an),
			un = String.fromCharCode(32),
			ln = {
				beforeInput: {
					phasedRegistrationNames: {
						bubbled: "onBeforeInput",
						captured: "onBeforeInputCapture"
					},
					dependencies: ["compositionend", "keypress", "textInput", "paste"]
				},
				compositionEnd: {
					phasedRegistrationNames: {
						bubbled: "onCompositionEnd",
						captured: "onCompositionEndCapture"
					},
					dependencies: "blur compositionend keydown keypress keyup mousedown".split(" ")
				},
				compositionStart: {
					phasedRegistrationNames: {
						bubbled: "onCompositionStart",
						captured: "onCompositionStartCapture"
					},
					dependencies: "blur compositionstart keydown keypress keyup mousedown".split(" ")
				},
				compositionUpdate: {
					phasedRegistrationNames: {
						bubbled: "onCompositionUpdate",
						captured: "onCompositionUpdateCapture"
					},
					dependencies: "blur compositionupdate keydown keypress keyup mousedown".split(" ")
				}
			},
			dn = !1;

		function pn(e, t) {
			switch (e) {
				case "keyup":
					return -1 !== nn.indexOf(t.keyCode);
				case "keydown":
					return 229 !== t.keyCode;
				case "keypress":
				case "mousedown":
				case "blur":
					return !0;
				default:
					return !1
			}
		}

		function fn(e) {
			return "object" == typeof(e = e.detail) && "data" in e ? e.data : null
		}
		var mn = !1;
		var gn = {
				eventTypes: ln,
				extractEvents: function(e, t, r, n) {
					var o;
					if (on) e: {
						switch (e) {
							case "compositionstart":
								var i = ln.compositionStart;
								break e;
							case "compositionend":
								i = ln.compositionEnd;
								break e;
							case "compositionupdate":
								i = ln.compositionUpdate;
								break e
						}
						i = void 0
					}
					else mn ? pn(e, r) && (i = ln.compositionEnd) : "keydown" === e && 229 === r.keyCode && (i = ln.compositionStart);
					return i ? (sn && "ko" !== r.locale && (mn || i !== ln.compositionStart ? i === ln.compositionEnd && mn && (o = $r()) : (Gr = "value" in (Wr = n) ? Wr.value : Wr.textContent, mn = !0)), i = tn.getPooled(i, t, r, n), o ? i.data = o : null !== (o = fn(r)) && (i.data = o), qr(i), o = i) : o = null, (e = cn ? function(e, t) {
						switch (e) {
							case "compositionend":
								return fn(t);
							case "keypress":
								return 32 !== t.which ? null : (dn = !0, un);
							case "textInput":
								return (e = t.data) === un && dn ? null : e;
							default:
								return null
						}
					}(e, r) : function(e, t) {
						if (mn) return "compositionend" === e || !on && pn(e, t) ? (e = $r(), Kr = Gr = Wr = null, mn = !1, e) : null;
						switch (e) {
							case "paste":
							default:
								return null;
							case "keypress":
								if (!(t.ctrlKey || t.altKey || t.metaKey) || t.ctrlKey && t.altKey) {
									if (t.char && 1 < t.char.length) return t.char;
									if (t.which) return String.fromCharCode(t.which)
								}
								return null;
							case "compositionend":
								return sn && "ko" !== t.locale ? null : t.data
						}
					}(e, r)) ? ((t = rn.getPooled(ln.beforeInput, t, r, n)).data = e, qr(t)) : t = null, null === o ? t : null === t ? o : [o, t]
				}
			},
			hn = {
				color: !0,
				date: !0,
				datetime: !0,
				"datetime-local": !0,
				email: !0,
				month: !0,
				number: !0,
				password: !0,
				range: !0,
				search: !0,
				tel: !0,
				text: !0,
				time: !0,
				url: !0,
				week: !0
			};

		function vn(e) {
			var t = e && e.nodeName && e.nodeName.toLowerCase();
			return "input" === t ? !!hn[e.type] : "textarea" === t
		}
		var bn = {
			change: {
				phasedRegistrationNames: {
					bubbled: "onChange",
					captured: "onChangeCapture"
				},
				dependencies: "blur change click focus input keydown keyup selectionchange".split(" ")
			}
		};

		function yn(e, t, r) {
			return (e = Yr.getPooled(bn.change, e, t, r)).type = "change", R(r), qr(e), e
		}
		var _n = null,
			wn = null;

		function kn(e) {
			lt(e)
		}

		function xn(e) {
			if (xe(zr(e))) return e
		}

		function Tn(e, t) {
			if ("change" === e) return t
		}
		var Sn = !1;

		function In() {
			_n && (_n.detachEvent("onpropertychange", En), wn = _n = null)
		}

		function En(e) {
			if ("value" === e.propertyName && xn(wn))
				if (e = yn(wn, e, dt(e)), U) lt(e);
				else {
					U = !0;
					try {
						z(kn, e)
					} finally {
						U = !1, H()
					}
				}
		}

		function Pn(e, t, r) {
			"focus" === e ? (In(), wn = r, (_n = t).attachEvent("onpropertychange", En)) : "blur" === e && In()
		}

		function Dn(e) {
			if ("selectionchange" === e || "keyup" === e || "keydown" === e) return xn(wn)
		}

		function Cn(e, t) {
			if ("click" === e) return xn(t)
		}

		function An(e, t) {
			if ("input" === e || "change" === e) return xn(t)
		}
		D && (Sn = pt("input") && (!document.documentMode || 9 < document.documentMode));
		var On = {
				eventTypes: bn,
				_isInputEventSupported: Sn,
				extractEvents: function(e, t, r, n) {
					var o = t ? zr(t) : window,
						i = o.nodeName && o.nodeName.toLowerCase();
					if ("select" === i || "input" === i && "file" === o.type) var a = Tn;
					else if (vn(o))
						if (Sn) a = An;
						else {
							a = Dn;
							var c = Pn
						}
					else(i = o.nodeName) && "input" === i.toLowerCase() && ("checkbox" === o.type || "radio" === o.type) && (a = Cn);
					if (a && (a = a(e, t))) return yn(a, r, n);
					c && c(e, o, t), "blur" === e && (e = o._wrapperState) && e.controlled && "number" === o.type && De(o, "number", o.value)
				}
			},
			Bn = Yr.extend({
				view: null,
				detail: null
			}),
			Rn = {
				Alt: "altKey",
				Control: "ctrlKey",
				Meta: "metaKey",
				Shift: "shiftKey"
			};

		function Mn(e) {
			var t = this.nativeEvent;
			return t.getModifierState ? t.getModifierState(e) : !!(e = Rn[e]) && !!t[e]
		}

		function zn() {
			return Mn
		}
		var Fn = 0,
			jn = 0,
			Ln = !1,
			Un = !1,
			Nn = Bn.extend({
				screenX: null,
				screenY: null,
				clientX: null,
				clientY: null,
				pageX: null,
				pageY: null,
				ctrlKey: null,
				shiftKey: null,
				altKey: null,
				metaKey: null,
				getModifierState: zn,
				button: null,
				buttons: null,
				relatedTarget: function(e) {
					return e.relatedTarget || (e.fromElement === e.srcElement ? e.toElement : e.fromElement)
				},
				movementX: function(e) {
					if ("movementX" in e) return e.movementX;
					var t = Fn;
					return Fn = e.screenX, Ln ? "mousemove" === e.type ? e.screenX - t : 0 : (Ln = !0, 0)
				},
				movementY: function(e) {
					if ("movementY" in e) return e.movementY;
					var t = jn;
					return jn = e.screenY, Un ? "mousemove" === e.type ? e.screenY - t : 0 : (Un = !0, 0)
				}
			}),
			Hn = Nn.extend({
				pointerId: null,
				width: null,
				height: null,
				pressure: null,
				tangentialPressure: null,
				tiltX: null,
				tiltY: null,
				twist: null,
				pointerType: null,
				isPrimary: null
			}),
			Vn = {
				mouseEnter: {
					registrationName: "onMouseEnter",
					dependencies: ["mouseout", "mouseover"]
				},
				mouseLeave: {
					registrationName: "onMouseLeave",
					dependencies: ["mouseout", "mouseover"]
				},
				pointerEnter: {
					registrationName: "onPointerEnter",
					dependencies: ["pointerout", "pointerover"]
				},
				pointerLeave: {
					registrationName: "onPointerLeave",
					dependencies: ["pointerout", "pointerover"]
				}
			},
			qn = {
				eventTypes: Vn,
				extractEvents: function(e, t, r, n, o) {
					var i = "mouseover" === e || "pointerover" === e,
						a = "mouseout" === e || "pointerout" === e;
					if (i && 0 == (32 & o) && (r.relatedTarget || r.fromElement) || !a && !i) return null;
					(i = n.window === n ? n : (i = n.ownerDocument) ? i.defaultView || i.parentWindow : window, a) ? (a = t, null !== (t = (t = r.relatedTarget || r.toElement) ? Rr(t) : null) && (t !== rt(t) || 5 !== t.tag && 6 !== t.tag) && (t = null)) : a = null;
					if (a === t) return null;
					if ("mouseout" === e || "mouseover" === e) var c = Nn,
						s = Vn.mouseLeave,
						u = Vn.mouseEnter,
						l = "mouse";
					else "pointerout" !== e && "pointerover" !== e || (c = Hn, s = Vn.pointerLeave, u = Vn.pointerEnter, l = "pointer");
					if (e = null == a ? i : zr(a), i = null == t ? i : zr(t), (s = c.getPooled(s, a, r, n)).type = l + "leave", s.target = e, s.relatedTarget = i, (r = c.getPooled(u, t, r, n)).type = l + "enter", r.target = i, r.relatedTarget = e, l = t, (n = a) && l) e: {
						for (u = l, a = 0, e = c = n; e; e = jr(e)) a++;
						for (e = 0, t = u; t; t = jr(t)) e++;
						for (; 0 < a - e;) c = jr(c),
						a--;
						for (; 0 < e - a;) u = jr(u),
						e--;
						for (; a--;) {
							if (c === u || c === u.alternate) break e;
							c = jr(c), u = jr(u)
						}
						c = null
					}
					else c = null;
					for (u = c, c = []; n && n !== u && (null === (a = n.alternate) || a !== u);) c.push(n), n = jr(n);
					for (n = []; l && l !== u && (null === (a = l.alternate) || a !== u);) n.push(l), l = jr(l);
					for (l = 0; l < c.length; l++) Hr(c[l], "bubbled", s);
					for (l = n.length; 0 < l--;) Hr(n[l], "captured", r);
					return 0 == (64 & o) ? [s] : [s, r]
				}
			};
		var Wn = "function" == typeof Object.is ? Object.is : function(e, t) {
				return e === t && (0 !== e || 1 / e == 1 / t) || e != e && t != t
			},
			Gn = Object.prototype.hasOwnProperty;

		function Kn(e, t) {
			if (Wn(e, t)) return !0;
			if ("object" != typeof e || null === e || "object" != typeof t || null === t) return !1;
			var r = Object.keys(e),
				n = Object.keys(t);
			if (r.length !== n.length) return !1;
			for (n = 0; n < r.length; n++)
				if (!Gn.call(t, r[n]) || !Wn(e[r[n]], t[r[n]])) return !1;
			return !0
		}
		var $n = D && "documentMode" in document && 11 >= document.documentMode,
			Qn = {
				select: {
					phasedRegistrationNames: {
						bubbled: "onSelect",
						captured: "onSelectCapture"
					},
					dependencies: "blur contextmenu dragend focus keydown keyup mousedown mouseup selectionchange".split(" ")
				}
			},
			Xn = null,
			Yn = null,
			Jn = null,
			Zn = !1;

		function eo(e, t) {
			var r = t.window === t ? t.document : 9 === t.nodeType ? t : t.ownerDocument;
			return Zn || null == Xn || Xn !== pr(r) ? null : ("selectionStart" in (r = Xn) && vr(r) ? r = {
				start: r.selectionStart,
				end: r.selectionEnd
			} : r = {
				anchorNode: (r = (r.ownerDocument && r.ownerDocument.defaultView || window).getSelection()).anchorNode,
				anchorOffset: r.anchorOffset,
				focusNode: r.focusNode,
				focusOffset: r.focusOffset
			}, Jn && Kn(Jn, r) ? null : (Jn = r, (e = Yr.getPooled(Qn.select, Yn, e, t)).type = "select", e.target = Xn, qr(e), e))
		}
		var to = {
				eventTypes: Qn,
				extractEvents: function(e, t, r, n, o, i) {
					if (!(i = !(o = i || (n.window === n ? n.document : 9 === n.nodeType ? n : n.ownerDocument)))) {
						e: {
							o = tt(o),
							i = E.onSelect;
							for (var a = 0; a < i.length; a++)
								if (!o.has(i[a])) {
									o = !1;
									break e
								} o = !0
						}
						i = !o
					}
					if (i) return null;
					switch (o = t ? zr(t) : window, e) {
						case "focus":
							(vn(o) || "true" === o.contentEditable) && (Xn = o, Yn = t, Jn = null);
							break;
						case "blur":
							Jn = Yn = Xn = null;
							break;
						case "mousedown":
							Zn = !0;
							break;
						case "contextmenu":
						case "mouseup":
						case "dragend":
							return Zn = !1, eo(r, n);
						case "selectionchange":
							if ($n) break;
						case "keydown":
						case "keyup":
							return eo(r, n)
					}
					return null
				}
			},
			ro = Yr.extend({
				animationName: null,
				elapsedTime: null,
				pseudoElement: null
			}),
			no = Yr.extend({
				clipboardData: function(e) {
					return "clipboardData" in e ? e.clipboardData : window.clipboardData
				}
			}),
			oo = Bn.extend({
				relatedTarget: null
			});

		function io(e) {
			var t = e.keyCode;
			return "charCode" in e ? 0 === (e = e.charCode) && 13 === t && (e = 13) : e = t, 10 === e && (e = 13), 32 <= e || 13 === e ? e : 0
		}
		var ao = {
				Esc: "Escape",
				Spacebar: " ",
				Left: "ArrowLeft",
				Up: "ArrowUp",
				Right: "ArrowRight",
				Down: "ArrowDown",
				Del: "Delete",
				Win: "OS",
				Menu: "ContextMenu",
				Apps: "ContextMenu",
				Scroll: "ScrollLock",
				MozPrintableKey: "Unidentified"
			},
			co = {
				8: "Backspace",
				9: "Tab",
				12: "Clear",
				13: "Enter",
				16: "Shift",
				17: "Control",
				18: "Alt",
				19: "Pause",
				20: "CapsLock",
				27: "Escape",
				32: " ",
				33: "PageUp",
				34: "PageDown",
				35: "End",
				36: "Home",
				37: "ArrowLeft",
				38: "ArrowUp",
				39: "ArrowRight",
				40: "ArrowDown",
				45: "Insert",
				46: "Delete",
				112: "F1",
				113: "F2",
				114: "F3",
				115: "F4",
				116: "F5",
				117: "F6",
				118: "F7",
				119: "F8",
				120: "F9",
				121: "F10",
				122: "F11",
				123: "F12",
				144: "NumLock",
				145: "ScrollLock",
				224: "Meta"
			},
			so = Bn.extend({
				key: function(e) {
					if (e.key) {
						var t = ao[e.key] || e.key;
						if ("Unidentified" !== t) return t
					}
					return "keypress" === e.type ? 13 === (e = io(e)) ? "Enter" : String.fromCharCode(e) : "keydown" === e.type || "keyup" === e.type ? co[e.keyCode] || "Unidentified" : ""
				},
				location: null,
				ctrlKey: null,
				shiftKey: null,
				altKey: null,
				metaKey: null,
				repeat: null,
				locale: null,
				getModifierState: zn,
				charCode: function(e) {
					return "keypress" === e.type ? io(e) : 0
				},
				keyCode: function(e) {
					return "keydown" === e.type || "keyup" === e.type ? e.keyCode : 0
				},
				which: function(e) {
					return "keypress" === e.type ? io(e) : "keydown" === e.type || "keyup" === e.type ? e.keyCode : 0
				}
			}),
			uo = Nn.extend({
				dataTransfer: null
			}),
			lo = Bn.extend({
				touches: null,
				targetTouches: null,
				changedTouches: null,
				altKey: null,
				metaKey: null,
				ctrlKey: null,
				shiftKey: null,
				getModifierState: zn
			}),
			po = Yr.extend({
				propertyName: null,
				elapsedTime: null,
				pseudoElement: null
			}),
			fo = Nn.extend({
				deltaX: function(e) {
					return "deltaX" in e ? e.deltaX : "wheelDeltaX" in e ? -e.wheelDeltaX : 0
				},
				deltaY: function(e) {
					return "deltaY" in e ? e.deltaY : "wheelDeltaY" in e ? -e.wheelDeltaY : "wheelDelta" in e ? -e.wheelDelta : 0
				},
				deltaZ: null,
				deltaMode: null
			}),
			mo = {
				eventTypes: Ut,
				extractEvents: function(e, t, r, n) {
					var o = Nt.get(e);
					if (!o) return null;
					switch (e) {
						case "keypress":
							if (0 === io(r)) return null;
						case "keydown":
						case "keyup":
							e = so;
							break;
						case "blur":
						case "focus":
							e = oo;
							break;
						case "click":
							if (2 === r.button) return null;
						case "auxclick":
						case "dblclick":
						case "mousedown":
						case "mousemove":
						case "mouseup":
						case "mouseout":
						case "mouseover":
						case "contextmenu":
							e = Nn;
							break;
						case "drag":
						case "dragend":
						case "dragenter":
						case "dragexit":
						case "dragleave":
						case "dragover":
						case "dragstart":
						case "drop":
							e = uo;
							break;
						case "touchcancel":
						case "touchend":
						case "touchmove":
						case "touchstart":
							e = lo;
							break;
						case Qe:
						case Xe:
						case Ye:
							e = ro;
							break;
						case Je:
							e = po;
							break;
						case "scroll":
							e = Bn;
							break;
						case "wheel":
							e = fo;
							break;
						case "copy":
						case "cut":
						case "paste":
							e = no;
							break;
						case "gotpointercapture":
						case "lostpointercapture":
						case "pointercancel":
						case "pointerdown":
						case "pointermove":
						case "pointerout":
						case "pointerover":
						case "pointerup":
							e = Hn;
							break;
						default:
							e = Yr
					}
					return qr(t = e.getPooled(o, t, r, n)), t
				}
			};
		if (y) throw Error(n(101));
		y = Array.prototype.slice.call("ResponderEventPlugin SimpleEventPlugin EnterLeaveEventPlugin ChangeEventPlugin SelectEventPlugin BeforeInputEventPlugin".split(" ")), w(), f = Fr, m = Mr, h = zr, P({
			SimpleEventPlugin: mo,
			EnterLeaveEventPlugin: qn,
			ChangeEventPlugin: On,
			SelectEventPlugin: to,
			BeforeInputEventPlugin: gn
		});
		var go = [],
			ho = -1;

		function vo(e) {
			0 > ho || (e.current = go[ho], go[ho] = null, ho--)
		}

		function bo(e, t) {
			ho++, go[ho] = e.current, e.current = t
		}
		var yo = {},
			_o = {
				current: yo
			},
			wo = {
				current: !1
			},
			ko = yo;

		function xo(e, t) {
			var r = e.type.contextTypes;
			if (!r) return yo;
			var n = e.stateNode;
			if (n && n.__reactInternalMemoizedUnmaskedChildContext === t) return n.__reactInternalMemoizedMaskedChildContext;
			var o, i = {};
			for (o in r) i[o] = t[o];
			return n && ((e = e.stateNode).__reactInternalMemoizedUnmaskedChildContext = t, e.__reactInternalMemoizedMaskedChildContext = i), i
		}

		function To(e) {
			return null != (e = e.childContextTypes)
		}

		function So() {
			vo(wo), vo(_o)
		}

		function Io(e, t, r) {
			if (_o.current !== yo) throw Error(n(168));
			bo(_o, t), bo(wo, r)
		}

		function Eo(e, r, o) {
			var i = e.stateNode;
			if (e = r.childContextTypes, "function" != typeof i.getChildContext) return o;
			for (var a in i = i.getChildContext())
				if (!(a in e)) throw Error(n(108, be(r) || "Unknown", a));
			return t({}, o, {}, i)
		}

		function Po(e) {
			return e = (e = e.stateNode) && e.__reactInternalMemoizedMergedChildContext || yo, ko = _o.current, bo(_o, e), bo(wo, wo.current), !0
		}

		function Do(e, t, r) {
			var o = e.stateNode;
			if (!o) throw Error(n(169));
			r ? (e = Eo(e, t, ko), o.__reactInternalMemoizedMergedChildContext = e, vo(wo), vo(_o), bo(_o, e)) : vo(wo), bo(wo, r)
		}
		var Co = r.unstable_runWithPriority,
			Ao = r.unstable_scheduleCallback,
			Oo = r.unstable_cancelCallback,
			Bo = r.unstable_requestPaint,
			Ro = r.unstable_now,
			Mo = r.unstable_getCurrentPriorityLevel,
			zo = r.unstable_ImmediatePriority,
			Fo = r.unstable_UserBlockingPriority,
			jo = r.unstable_NormalPriority,
			Lo = r.unstable_LowPriority,
			Uo = r.unstable_IdlePriority,
			No = {},
			Ho = r.unstable_shouldYield,
			Vo = void 0 !== Bo ? Bo : function() {},
			qo = null,
			Wo = null,
			Go = !1,
			Ko = Ro(),
			$o = 1e4 > Ko ? Ro : function() {
				return Ro() - Ko
			};

		function Qo() {
			switch (Mo()) {
				case zo:
					return 99;
				case Fo:
					return 98;
				case jo:
					return 97;
				case Lo:
					return 96;
				case Uo:
					return 95;
				default:
					throw Error(n(332))
			}
		}

		function Xo(e) {
			switch (e) {
				case 99:
					return zo;
				case 98:
					return Fo;
				case 97:
					return jo;
				case 96:
					return Lo;
				case 95:
					return Uo;
				default:
					throw Error(n(332))
			}
		}

		function Yo(e, t) {
			return e = Xo(e), Co(e, t)
		}

		function Jo(e, t, r) {
			return e = Xo(e), Ao(e, t, r)
		}

		function Zo(e) {
			return null === qo ? (qo = [e], Wo = Ao(zo, ti)) : qo.push(e), No
		}

		function ei() {
			if (null !== Wo) {
				var e = Wo;
				Wo = null, Oo(e)
			}
			ti()
		}

		function ti() {
			if (!Go && null !== qo) {
				Go = !0;
				var e = 0;
				try {
					var t = qo;
					Yo(99, (function() {
						for (; e < t.length; e++) {
							var r = t[e];
							do {
								r = r(!0)
							} while (null !== r)
						}
					})), qo = null
				} catch (t) {
					throw null !== qo && (qo = qo.slice(e + 1)), Ao(zo, ei), t
				} finally {
					Go = !1
				}
			}
		}

		function ri(e, t, r) {
			return 1073741821 - (1 + ((1073741821 - e + t / 10) / (r /= 10) | 0)) * r
		}

		function ni(e, r) {
			if (e && e.defaultProps)
				for (var n in r = t({}, r), e = e.defaultProps) void 0 === r[n] && (r[n] = e[n]);
			return r
		}
		var oi = {
				current: null
			},
			ii = null,
			ai = null,
			ci = null;

		function si() {
			ci = ai = ii = null
		}

		function ui(e) {
			var t = oi.current;
			vo(oi), e.type._context._currentValue = t
		}

		function li(e, t) {
			for (; null !== e;) {
				var r = e.alternate;
				if (e.childExpirationTime < t) e.childExpirationTime = t, null !== r && r.childExpirationTime < t && (r.childExpirationTime = t);
				else {
					if (!(null !== r && r.childExpirationTime < t)) break;
					r.childExpirationTime = t
				}
				e = e.return
			}
		}

		function di(e, t) {
			ii = e, ci = ai = null, null !== (e = e.dependencies) && null !== e.firstContext && (e.expirationTime >= t && (ja = !0), e.firstContext = null)
		}

		function pi(e, t) {
			if (ci !== e && !1 !== t && 0 !== t)
				if ("number" == typeof t && 1073741823 !== t || (ci = e, t = 1073741823), t = {
						context: e,
						observedBits: t,
						next: null
					}, null === ai) {
					if (null === ii) throw Error(n(308));
					ai = t, ii.dependencies = {
						expirationTime: 0,
						firstContext: t,
						responders: null
					}
				} else ai = ai.next = t;
			return e._currentValue
		}
		var fi = !1;

		function mi(e) {
			e.updateQueue = {
				baseState: e.memoizedState,
				baseQueue: null,
				shared: {
					pending: null
				},
				effects: null
			}
		}

		function gi(e, t) {
			e = e.updateQueue, t.updateQueue === e && (t.updateQueue = {
				baseState: e.baseState,
				baseQueue: e.baseQueue,
				shared: e.shared,
				effects: e.effects
			})
		}

		function hi(e, t) {
			return (e = {
				expirationTime: e,
				suspenseConfig: t,
				tag: 0,
				payload: null,
				callback: null,
				next: null
			}).next = e
		}

		function vi(e, t) {
			if (null !== (e = e.updateQueue)) {
				var r = (e = e.shared).pending;
				null === r ? t.next = t : (t.next = r.next, r.next = t), e.pending = t
			}
		}

		function bi(e, t) {
			var r = e.alternate;
			null !== r && gi(r, e), null === (r = (e = e.updateQueue).baseQueue) ? (e.baseQueue = t.next = t, t.next = t) : (t.next = r.next, r.next = t)
		}

		function yi(e, r, n, o) {
			var i = e.updateQueue;
			fi = !1;
			var a = i.baseQueue,
				c = i.shared.pending;
			if (null !== c) {
				if (null !== a) {
					var s = a.next;
					a.next = c.next, c.next = s
				}
				a = c, i.shared.pending = null, null !== (s = e.alternate) && (null !== (s = s.updateQueue) && (s.baseQueue = c))
			}
			if (null !== a) {
				s = a.next;
				var u = i.baseState,
					l = 0,
					d = null,
					p = null,
					f = null;
				if (null !== s)
					for (var m = s;;) {
						if ((c = m.expirationTime) < o) {
							var g = {
								expirationTime: m.expirationTime,
								suspenseConfig: m.suspenseConfig,
								tag: m.tag,
								payload: m.payload,
								callback: m.callback,
								next: null
							};
							null === f ? (p = f = g, d = u) : f = f.next = g, c > l && (l = c)
						} else {
							null !== f && (f = f.next = {
								expirationTime: 1073741823,
								suspenseConfig: m.suspenseConfig,
								tag: m.tag,
								payload: m.payload,
								callback: m.callback,
								next: null
							}), Ts(c, m.suspenseConfig);
							e: {
								var h = e,
									v = m;
								switch (c = r, g = n, v.tag) {
									case 1:
										if ("function" == typeof(h = v.payload)) {
											u = h.call(g, u, c);
											break e
										}
										u = h;
										break e;
									case 3:
										h.effectTag = -4097 & h.effectTag | 64;
									case 0:
										if (null == (c = "function" == typeof(h = v.payload) ? h.call(g, u, c) : h)) break e;
										u = t({}, u, c);
										break e;
									case 2:
										fi = !0
								}
							}
							null !== m.callback && (e.effectTag |= 32, null === (c = i.effects) ? i.effects = [m] : c.push(m))
						}
						if (null === (m = m.next) || m === s) {
							if (null === (c = i.shared.pending)) break;
							m = a.next = c.next, c.next = s, i.baseQueue = a = c, i.shared.pending = null
						}
					}
				null === f ? d = u : f.next = p, i.baseState = d, i.baseQueue = f, Ss(l), e.expirationTime = l, e.memoizedState = u
			}
		}

		function _i(e, t, r) {
			if (e = t.effects, t.effects = null, null !== e)
				for (t = 0; t < e.length; t++) {
					var o = e[t],
						i = o.callback;
					if (null !== i) {
						if (o.callback = null, o = i, i = r, "function" != typeof o) throw Error(n(191, o));
						o.call(i)
					}
				}
		}
		var wi = J.ReactCurrentBatchConfig,
			ki = (new e.Component).refs;

		function xi(e, r, n, o) {
			n = null == (n = n(o, r = e.memoizedState)) ? r : t({}, r, n), e.memoizedState = n, 0 === e.expirationTime && (e.updateQueue.baseState = n)
		}
		var Ti = {
			isMounted: function(e) {
				return !!(e = e._reactInternalFiber) && rt(e) === e
			},
			enqueueSetState: function(e, t, r) {
				e = e._reactInternalFiber;
				var n = ds(),
					o = wi.suspense;
				(o = hi(n = ps(n, e, o), o)).payload = t, null != r && (o.callback = r), vi(e, o), fs(e, n)
			},
			enqueueReplaceState: function(e, t, r) {
				e = e._reactInternalFiber;
				var n = ds(),
					o = wi.suspense;
				(o = hi(n = ps(n, e, o), o)).tag = 1, o.payload = t, null != r && (o.callback = r), vi(e, o), fs(e, n)
			},
			enqueueForceUpdate: function(e, t) {
				e = e._reactInternalFiber;
				var r = ds(),
					n = wi.suspense;
				(n = hi(r = ps(r, e, n), n)).tag = 2, null != t && (n.callback = t), vi(e, n), fs(e, r)
			}
		};

		function Si(e, t, r, n, o, i, a) {
			return "function" == typeof(e = e.stateNode).shouldComponentUpdate ? e.shouldComponentUpdate(n, i, a) : !t.prototype || !t.prototype.isPureReactComponent || (!Kn(r, n) || !Kn(o, i))
		}

		function Ii(e, t, r) {
			var n = !1,
				o = yo,
				i = t.contextType;
			return "object" == typeof i && null !== i ? i = pi(i) : (o = To(t) ? ko : _o.current, i = (n = null != (n = t.contextTypes)) ? xo(e, o) : yo), t = new t(r, i), e.memoizedState = null !== t.state && void 0 !== t.state ? t.state : null, t.updater = Ti, e.stateNode = t, t._reactInternalFiber = e, n && ((e = e.stateNode).__reactInternalMemoizedUnmaskedChildContext = o, e.__reactInternalMemoizedMaskedChildContext = i), t
		}

		function Ei(e, t, r, n) {
			e = t.state, "function" == typeof t.componentWillReceiveProps && t.componentWillReceiveProps(r, n), "function" == typeof t.UNSAFE_componentWillReceiveProps && t.UNSAFE_componentWillReceiveProps(r, n), t.state !== e && Ti.enqueueReplaceState(t, t.state, null)
		}

		function Pi(e, t, r, n) {
			var o = e.stateNode;
			o.props = r, o.state = e.memoizedState, o.refs = ki, mi(e);
			var i = t.contextType;
			"object" == typeof i && null !== i ? o.context = pi(i) : (i = To(t) ? ko : _o.current, o.context = xo(e, i)), yi(e, r, o, n), o.state = e.memoizedState, "function" == typeof(i = t.getDerivedStateFromProps) && (xi(e, t, i, r), o.state = e.memoizedState), "function" == typeof t.getDerivedStateFromProps || "function" == typeof o.getSnapshotBeforeUpdate || "function" != typeof o.UNSAFE_componentWillMount && "function" != typeof o.componentWillMount || (t = o.state, "function" == typeof o.componentWillMount && o.componentWillMount(), "function" == typeof o.UNSAFE_componentWillMount && o.UNSAFE_componentWillMount(), t !== o.state && Ti.enqueueReplaceState(o, o.state, null), yi(e, r, o, n), o.state = e.memoizedState), "function" == typeof o.componentDidMount && (e.effectTag |= 4)
		}
		var Di = Array.isArray;

		function Ci(e, t, r) {
			if (null !== (e = r.ref) && "function" != typeof e && "object" != typeof e) {
				if (r._owner) {
					if (r = r._owner) {
						if (1 !== r.tag) throw Error(n(309));
						var o = r.stateNode
					}
					if (!o) throw Error(n(147, e));
					var i = "" + e;
					return null !== t && null !== t.ref && "function" == typeof t.ref && t.ref._stringRef === i ? t.ref : (t = function(e) {
						var t = o.refs;
						t === ki && (t = o.refs = {}), null === e ? delete t[i] : t[i] = e
					}, t._stringRef = i, t)
				}
				if ("string" != typeof e) throw Error(n(284));
				if (!r._owner) throw Error(n(290, e))
			}
			return e
		}

		function Ai(e, t) {
			if ("textarea" !== e.type) throw Error(n(31, "[object Object]" === Object.prototype.toString.call(t) ? "object with keys {" + Object.keys(t).join(", ") + "}" : t, ""))
		}

		function Oi(e) {
			function t(t, r) {
				if (e) {
					var n = t.lastEffect;
					null !== n ? (n.nextEffect = r, t.lastEffect = r) : t.firstEffect = t.lastEffect = r, r.nextEffect = null, r.effectTag = 8
				}
			}

			function r(r, n) {
				if (!e) return null;
				for (; null !== n;) t(r, n), n = n.sibling;
				return null
			}

			function o(e, t) {
				for (e = new Map; null !== t;) null !== t.key ? e.set(t.key, t) : e.set(t.index, t), t = t.sibling;
				return e
			}

			function i(e, t) {
				return (e = Ws(e, t)).index = 0, e.sibling = null, e
			}

			function a(t, r, n) {
				return t.index = n, e ? null !== (n = t.alternate) ? (n = n.index) < r ? (t.effectTag = 2, r) : n : (t.effectTag = 2, r) : r
			}

			function c(t) {
				return e && null === t.alternate && (t.effectTag = 2), t
			}

			function s(e, t, r, n) {
				return null === t || 6 !== t.tag ? ((t = $s(r, e.mode, n)).return = e, t) : ((t = i(t, r)).return = e, t)
			}

			function u(e, t, r, n) {
				return null !== t && t.elementType === r.type ? ((n = i(t, r.props)).ref = Ci(e, t, r), n.return = e, n) : ((n = Gs(r.type, r.key, r.props, null, e.mode, n)).ref = Ci(e, t, r), n.return = e, n)
			}

			function l(e, t, r, n) {
				return null === t || 4 !== t.tag || t.stateNode.containerInfo !== r.containerInfo || t.stateNode.implementation !== r.implementation ? ((t = Qs(r, e.mode, n)).return = e, t) : ((t = i(t, r.children || [])).return = e, t)
			}

			function d(e, t, r, n, o) {
				return null === t || 7 !== t.tag ? ((t = Ks(r, e.mode, n, o)).return = e, t) : ((t = i(t, r)).return = e, t)
			}

			function p(e, t, r) {
				if ("string" == typeof t || "number" == typeof t) return (t = $s("" + t, e.mode, r)).return = e, t;
				if ("object" == typeof t && null !== t) {
					switch (t.$$typeof) {
						case re:
							return (r = Gs(t.type, t.key, t.props, null, e.mode, r)).ref = Ci(e, null, t), r.return = e, r;
						case ne:
							return (t = Qs(t, e.mode, r)).return = e, t
					}
					if (Di(t) || ve(t)) return (t = Ks(t, e.mode, r, null)).return = e, t;
					Ai(e, t)
				}
				return null
			}

			function f(e, t, r, n) {
				var o = null !== t ? t.key : null;
				if ("string" == typeof r || "number" == typeof r) return null !== o ? null : s(e, t, "" + r, n);
				if ("object" == typeof r && null !== r) {
					switch (r.$$typeof) {
						case re:
							return r.key === o ? r.type === oe ? d(e, t, r.props.children, n, o) : u(e, t, r, n) : null;
						case ne:
							return r.key === o ? l(e, t, r, n) : null
					}
					if (Di(r) || ve(r)) return null !== o ? null : d(e, t, r, n, null);
					Ai(e, r)
				}
				return null
			}

			function m(e, t, r, n, o) {
				if ("string" == typeof n || "number" == typeof n) return s(t, e = e.get(r) || null, "" + n, o);
				if ("object" == typeof n && null !== n) {
					switch (n.$$typeof) {
						case re:
							return e = e.get(null === n.key ? r : n.key) || null, n.type === oe ? d(t, e, n.props.children, o, n.key) : u(t, e, n, o);
						case ne:
							return l(t, e = e.get(null === n.key ? r : n.key) || null, n, o)
					}
					if (Di(n) || ve(n)) return d(t, e = e.get(r) || null, n, o, null);
					Ai(t, n)
				}
				return null
			}

			function g(n, i, c, s) {
				for (var u = null, l = null, d = i, g = i = 0, h = null; null !== d && g < c.length; g++) {
					d.index > g ? (h = d, d = null) : h = d.sibling;
					var v = f(n, d, c[g], s);
					if (null === v) {
						null === d && (d = h);
						break
					}
					e && d && null === v.alternate && t(n, d), i = a(v, i, g), null === l ? u = v : l.sibling = v, l = v, d = h
				}
				if (g === c.length) return r(n, d), u;
				if (null === d) {
					for (; g < c.length; g++) null !== (d = p(n, c[g], s)) && (i = a(d, i, g), null === l ? u = d : l.sibling = d, l = d);
					return u
				}
				for (d = o(n, d); g < c.length; g++) null !== (h = m(d, n, g, c[g], s)) && (e && null !== h.alternate && d.delete(null === h.key ? g : h.key), i = a(h, i, g), null === l ? u = h : l.sibling = h, l = h);
				return e && d.forEach((function(e) {
					return t(n, e)
				})), u
			}

			function h(i, c, s, u) {
				var l = ve(s);
				if ("function" != typeof l) throw Error(n(150));
				if (null == (s = l.call(s))) throw Error(n(151));
				for (var d = l = null, g = c, h = c = 0, v = null, b = s.next(); null !== g && !b.done; h++, b = s.next()) {
					g.index > h ? (v = g, g = null) : v = g.sibling;
					var y = f(i, g, b.value, u);
					if (null === y) {
						null === g && (g = v);
						break
					}
					e && g && null === y.alternate && t(i, g), c = a(y, c, h), null === d ? l = y : d.sibling = y, d = y, g = v
				}
				if (b.done) return r(i, g), l;
				if (null === g) {
					for (; !b.done; h++, b = s.next()) null !== (b = p(i, b.value, u)) && (c = a(b, c, h), null === d ? l = b : d.sibling = b, d = b);
					return l
				}
				for (g = o(i, g); !b.done; h++, b = s.next()) null !== (b = m(g, i, h, b.value, u)) && (e && null !== b.alternate && g.delete(null === b.key ? h : b.key), c = a(b, c, h), null === d ? l = b : d.sibling = b, d = b);
				return e && g.forEach((function(e) {
					return t(i, e)
				})), l
			}
			return function(e, o, a, s) {
				var u = "object" == typeof a && null !== a && a.type === oe && null === a.key;
				u && (a = a.props.children);
				var l = "object" == typeof a && null !== a;
				if (l) switch (a.$$typeof) {
					case re:
						e: {
							for (l = a.key, u = o; null !== u;) {
								if (u.key === l) {
									if (7 === u.tag) {
										if (a.type === oe) {
											r(e, u.sibling), (o = i(u, a.props.children)).return = e, e = o;
											break e
										}
									} else if (u.elementType === a.type) {
										r(e, u.sibling), (o = i(u, a.props)).ref = Ci(e, u, a), o.return = e, e = o;
										break e
									}
									r(e, u);
									break
								}
								t(e, u), u = u.sibling
							}
							a.type === oe ? ((o = Ks(a.props.children, e.mode, s, a.key)).return = e, e = o) : ((s = Gs(a.type, a.key, a.props, null, e.mode, s)).ref = Ci(e, o, a), s.return = e, e = s)
						}
						return c(e);
					case ne:
						e: {
							for (u = a.key; null !== o;) {
								if (o.key === u) {
									if (4 === o.tag && o.stateNode.containerInfo === a.containerInfo && o.stateNode.implementation === a.implementation) {
										r(e, o.sibling), (o = i(o, a.children || [])).return = e, e = o;
										break e
									}
									r(e, o);
									break
								}
								t(e, o), o = o.sibling
							}(o = Qs(a, e.mode, s)).return = e,
							e = o
						}
						return c(e)
				}
				if ("string" == typeof a || "number" == typeof a) return a = "" + a, null !== o && 6 === o.tag ? (r(e, o.sibling), (o = i(o, a)).return = e, e = o) : (r(e, o), (o = $s(a, e.mode, s)).return = e, e = o), c(e);
				if (Di(a)) return g(e, o, a, s);
				if (ve(a)) return h(e, o, a, s);
				if (l && Ai(e, a), void 0 === a && !u) switch (e.tag) {
					case 1:
					case 0:
						throw e = e.type, Error(n(152, e.displayName || e.name || "Component"))
				}
				return r(e, o)
			}
		}
		var Bi = Oi(!0),
			Ri = Oi(!1),
			Mi = {},
			zi = {
				current: Mi
			},
			Fi = {
				current: Mi
			},
			ji = {
				current: Mi
			};

		function Li(e) {
			if (e === Mi) throw Error(n(174));
			return e
		}

		function Ui(e, t) {
			switch (bo(ji, t), bo(Fi, e), bo(zi, Mi), e = t.nodeType) {
				case 9:
				case 11:
					t = (t = t.documentElement) ? t.namespaceURI : Le(null, "");
					break;
				default:
					t = Le(t = (e = 8 === e ? t.parentNode : t).namespaceURI || null, e = e.tagName)
			}
			vo(zi), bo(zi, t)
		}

		function Ni() {
			vo(zi), vo(Fi), vo(ji)
		}

		function Hi(e) {
			Li(ji.current);
			var t = Li(zi.current),
				r = Le(t, e.type);
			t !== r && (bo(Fi, e), bo(zi, r))
		}

		function Vi(e) {
			Fi.current === e && (vo(zi), vo(Fi))
		}
		var qi = {
			current: 0
		};

		function Wi(e) {
			for (var t = e; null !== t;) {
				if (13 === t.tag) {
					var r = t.memoizedState;
					if (null !== r && (null === (r = r.dehydrated) || r.data === _r || r.data === wr)) return t
				} else if (19 === t.tag && void 0 !== t.memoizedProps.revealOrder) {
					if (0 != (64 & t.effectTag)) return t
				} else if (null !== t.child) {
					t.child.return = t, t = t.child;
					continue
				}
				if (t === e) break;
				for (; null === t.sibling;) {
					if (null === t.return || t.return === e) return null;
					t = t.return
				}
				t.sibling.return = t.return, t = t.sibling
			}
			return null
		}

		function Gi(e, t) {
			return {
				responder: e,
				props: t
			}
		}
		var Ki = J.ReactCurrentDispatcher,
			$i = J.ReactCurrentBatchConfig,
			Qi = 0,
			Xi = null,
			Yi = null,
			Ji = null,
			Zi = !1;

		function ea() {
			throw Error(n(321))
		}

		function ta(e, t) {
			if (null === t) return !1;
			for (var r = 0; r < t.length && r < e.length; r++)
				if (!Wn(e[r], t[r])) return !1;
			return !0
		}

		function ra(e, t, r, o, i, a) {
			if (Qi = a, Xi = t, t.memoizedState = null, t.updateQueue = null, t.expirationTime = 0, Ki.current = null === e || null === e.memoizedState ? Sa : Ia, e = r(o, i), t.expirationTime === Qi) {
				a = 0;
				do {
					if (t.expirationTime = 0, !(25 > a)) throw Error(n(301));
					a += 1, Ji = Yi = null, t.updateQueue = null, Ki.current = Ea, e = r(o, i)
				} while (t.expirationTime === Qi)
			}
			if (Ki.current = Ta, t = null !== Yi && null !== Yi.next, Qi = 0, Ji = Yi = Xi = null, Zi = !1, t) throw Error(n(300));
			return e
		}

		function na() {
			var e = {
				memoizedState: null,
				baseState: null,
				baseQueue: null,
				queue: null,
				next: null
			};
			return null === Ji ? Xi.memoizedState = Ji = e : Ji = Ji.next = e, Ji
		}

		function oa() {
			if (null === Yi) {
				var e = Xi.alternate;
				e = null !== e ? e.memoizedState : null
			} else e = Yi.next;
			var t = null === Ji ? Xi.memoizedState : Ji.next;
			if (null !== t) Ji = t, Yi = e;
			else {
				if (null === e) throw Error(n(310));
				e = {
					memoizedState: (Yi = e).memoizedState,
					baseState: Yi.baseState,
					baseQueue: Yi.baseQueue,
					queue: Yi.queue,
					next: null
				}, null === Ji ? Xi.memoizedState = Ji = e : Ji = Ji.next = e
			}
			return Ji
		}

		function ia(e, t) {
			return "function" == typeof t ? t(e) : t
		}

		function aa(e) {
			var t = oa(),
				r = t.queue;
			if (null === r) throw Error(n(311));
			r.lastRenderedReducer = e;
			var o = Yi,
				i = o.baseQueue,
				a = r.pending;
			if (null !== a) {
				if (null !== i) {
					var c = i.next;
					i.next = a.next, a.next = c
				}
				o.baseQueue = i = a, r.pending = null
			}
			if (null !== i) {
				i = i.next, o = o.baseState;
				var s = c = a = null,
					u = i;
				do {
					var l = u.expirationTime;
					if (l < Qi) {
						var d = {
							expirationTime: u.expirationTime,
							suspenseConfig: u.suspenseConfig,
							action: u.action,
							eagerReducer: u.eagerReducer,
							eagerState: u.eagerState,
							next: null
						};
						null === s ? (c = s = d, a = o) : s = s.next = d, l > Xi.expirationTime && (Xi.expirationTime = l, Ss(l))
					} else null !== s && (s = s.next = {
						expirationTime: 1073741823,
						suspenseConfig: u.suspenseConfig,
						action: u.action,
						eagerReducer: u.eagerReducer,
						eagerState: u.eagerState,
						next: null
					}), Ts(l, u.suspenseConfig), o = u.eagerReducer === e ? u.eagerState : e(o, u.action);
					u = u.next
				} while (null !== u && u !== i);
				null === s ? a = o : s.next = c, Wn(o, t.memoizedState) || (ja = !0), t.memoizedState = o, t.baseState = a, t.baseQueue = s, r.lastRenderedState = o
			}
			return [t.memoizedState, r.dispatch]
		}

		function ca(e) {
			var t = oa(),
				r = t.queue;
			if (null === r) throw Error(n(311));
			r.lastRenderedReducer = e;
			var o = r.dispatch,
				i = r.pending,
				a = t.memoizedState;
			if (null !== i) {
				r.pending = null;
				var c = i = i.next;
				do {
					a = e(a, c.action), c = c.next
				} while (c !== i);
				Wn(a, t.memoizedState) || (ja = !0), t.memoizedState = a, null === t.baseQueue && (t.baseState = a), r.lastRenderedState = a
			}
			return [a, o]
		}

		function sa(e) {
			var t = na();
			return "function" == typeof e && (e = e()), t.memoizedState = t.baseState = e, e = (e = t.queue = {
				pending: null,
				dispatch: null,
				lastRenderedReducer: ia,
				lastRenderedState: e
			}).dispatch = xa.bind(null, Xi, e), [t.memoizedState, e]
		}

		function ua(e, t, r, n) {
			return e = {
				tag: e,
				create: t,
				destroy: r,
				deps: n,
				next: null
			}, null === (t = Xi.updateQueue) ? (t = {
				lastEffect: null
			}, Xi.updateQueue = t, t.lastEffect = e.next = e) : null === (r = t.lastEffect) ? t.lastEffect = e.next = e : (n = r.next, r.next = e, e.next = n, t.lastEffect = e), e
		}

		function la() {
			return oa().memoizedState
		}

		function da(e, t, r, n) {
			var o = na();
			Xi.effectTag |= e, o.memoizedState = ua(1 | t, r, void 0, void 0 === n ? null : n)
		}

		function pa(e, t, r, n) {
			var o = oa();
			n = void 0 === n ? null : n;
			var i = void 0;
			if (null !== Yi) {
				var a = Yi.memoizedState;
				if (i = a.destroy, null !== n && ta(n, a.deps)) return void ua(t, r, i, n)
			}
			Xi.effectTag |= e, o.memoizedState = ua(1 | t, r, i, n)
		}

		function fa(e, t) {
			return da(516, 4, e, t)
		}

		function ma(e, t) {
			return pa(516, 4, e, t)
		}

		function ga(e, t) {
			return pa(4, 2, e, t)
		}

		function ha(e, t) {
			return "function" == typeof t ? (e = e(), t(e), function() {
				t(null)
			}) : null != t ? (e = e(), t.current = e, function() {
				t.current = null
			}) : void 0
		}

		function va(e, t, r) {
			return r = null != r ? r.concat([e]) : null, pa(4, 2, ha.bind(null, t, e), r)
		}

		function ba() {}

		function ya(e, t) {
			return na().memoizedState = [e, void 0 === t ? null : t], e
		}

		function _a(e, t) {
			var r = oa();
			t = void 0 === t ? null : t;
			var n = r.memoizedState;
			return null !== n && null !== t && ta(t, n[1]) ? n[0] : (r.memoizedState = [e, t], e)
		}

		function wa(e, t) {
			var r = oa();
			t = void 0 === t ? null : t;
			var n = r.memoizedState;
			return null !== n && null !== t && ta(t, n[1]) ? n[0] : (e = e(), r.memoizedState = [e, t], e)
		}

		function ka(e, t, r) {
			var n = Qo();
			Yo(98 > n ? 98 : n, (function() {
				e(!0)
			})), Yo(97 < n ? 97 : n, (function() {
				var n = $i.suspense;
				$i.suspense = void 0 === t ? null : t;
				try {
					e(!1), r()
				} finally {
					$i.suspense = n
				}
			}))
		}

		function xa(e, t, r) {
			var n = ds(),
				o = wi.suspense;
			o = {
				expirationTime: n = ps(n, e, o),
				suspenseConfig: o,
				action: r,
				eagerReducer: null,
				eagerState: null,
				next: null
			};
			var i = t.pending;
			if (null === i ? o.next = o : (o.next = i.next, i.next = o), t.pending = o, i = e.alternate, e === Xi || null !== i && i === Xi) Zi = !0, o.expirationTime = Qi, Xi.expirationTime = Qi;
			else {
				if (0 === e.expirationTime && (null === i || 0 === i.expirationTime) && null !== (i = t.lastRenderedReducer)) try {
					var a = t.lastRenderedState,
						c = i(a, r);
					if (o.eagerReducer = i, o.eagerState = c, Wn(c, a)) return
				} catch (e) {}
				fs(e, n)
			}
		}
		var Ta = {
				readContext: pi,
				useCallback: ea,
				useContext: ea,
				useEffect: ea,
				useImperativeHandle: ea,
				useLayoutEffect: ea,
				useMemo: ea,
				useReducer: ea,
				useRef: ea,
				useState: ea,
				useDebugValue: ea,
				useResponder: ea,
				useDeferredValue: ea,
				useTransition: ea
			},
			Sa = {
				readContext: pi,
				useCallback: ya,
				useContext: pi,
				useEffect: fa,
				useImperativeHandle: function(e, t, r) {
					return r = null != r ? r.concat([e]) : null, da(4, 2, ha.bind(null, t, e), r)
				},
				useLayoutEffect: function(e, t) {
					return da(4, 2, e, t)
				},
				useMemo: function(e, t) {
					var r = na();
					return t = void 0 === t ? null : t, e = e(), r.memoizedState = [e, t], e
				},
				useReducer: function(e, t, r) {
					var n = na();
					return t = void 0 !== r ? r(t) : t, n.memoizedState = n.baseState = t, e = (e = n.queue = {
						pending: null,
						dispatch: null,
						lastRenderedReducer: e,
						lastRenderedState: t
					}).dispatch = xa.bind(null, Xi, e), [n.memoizedState, e]
				},
				useRef: function(e) {
					return e = {
						current: e
					}, na().memoizedState = e
				},
				useState: sa,
				useDebugValue: ba,
				useResponder: Gi,
				useDeferredValue: function(e, t) {
					var r = sa(e),
						n = r[0],
						o = r[1];
					return fa((function() {
						var r = $i.suspense;
						$i.suspense = void 0 === t ? null : t;
						try {
							o(e)
						} finally {
							$i.suspense = r
						}
					}), [e, t]), n
				},
				useTransition: function(e) {
					var t = sa(!1),
						r = t[0];
					return t = t[1], [ya(ka.bind(null, t, e), [t, e]), r]
				}
			},
			Ia = {
				readContext: pi,
				useCallback: _a,
				useContext: pi,
				useEffect: ma,
				useImperativeHandle: va,
				useLayoutEffect: ga,
				useMemo: wa,
				useReducer: aa,
				useRef: la,
				useState: function() {
					return aa(ia)
				},
				useDebugValue: ba,
				useResponder: Gi,
				useDeferredValue: function(e, t) {
					var r = aa(ia),
						n = r[0],
						o = r[1];
					return ma((function() {
						var r = $i.suspense;
						$i.suspense = void 0 === t ? null : t;
						try {
							o(e)
						} finally {
							$i.suspense = r
						}
					}), [e, t]), n
				},
				useTransition: function(e) {
					var t = aa(ia),
						r = t[0];
					return t = t[1], [_a(ka.bind(null, t, e), [t, e]), r]
				}
			},
			Ea = {
				readContext: pi,
				useCallback: _a,
				useContext: pi,
				useEffect: ma,
				useImperativeHandle: va,
				useLayoutEffect: ga,
				useMemo: wa,
				useReducer: ca,
				useRef: la,
				useState: function() {
					return ca(ia)
				},
				useDebugValue: ba,
				useResponder: Gi,
				useDeferredValue: function(e, t) {
					var r = ca(ia),
						n = r[0],
						o = r[1];
					return ma((function() {
						var r = $i.suspense;
						$i.suspense = void 0 === t ? null : t;
						try {
							o(e)
						} finally {
							$i.suspense = r
						}
					}), [e, t]), n
				},
				useTransition: function(e) {
					var t = ca(ia),
						r = t[0];
					return t = t[1], [_a(ka.bind(null, t, e), [t, e]), r]
				}
			},
			Pa = null,
			Da = null,
			Ca = !1;

		function Aa(e, t) {
			var r = Vs(5, null, null, 0);
			r.elementType = "DELETED", r.type = "DELETED", r.stateNode = t, r.return = e, r.effectTag = 8, null !== e.lastEffect ? (e.lastEffect.nextEffect = r, e.lastEffect = r) : e.firstEffect = e.lastEffect = r
		}

		function Oa(e, t) {
			switch (e.tag) {
				case 5:
					var r = e.type;
					return null !== (t = 1 !== t.nodeType || r.toLowerCase() !== t.nodeName.toLowerCase() ? null : t) && (e.stateNode = t, !0);
				case 6:
					return null !== (t = "" === e.pendingProps || 3 !== t.nodeType ? null : t) && (e.stateNode = t, !0);
				default:
					return !1
			}
		}

		function Ba(e) {
			if (Ca) {
				var t = Da;
				if (t) {
					var r = t;
					if (!Oa(e, t)) {
						if (!(t = Pr(r.nextSibling)) || !Oa(e, t)) return e.effectTag = -1025 & e.effectTag | 2, Ca = !1, void(Pa = e);
						Aa(Pa, r)
					}
					Pa = e, Da = Pr(t.firstChild)
				} else e.effectTag = -1025 & e.effectTag | 2, Ca = !1, Pa = e
			}
		}

		function Ra(e) {
			for (e = e.return; null !== e && 5 !== e.tag && 3 !== e.tag && 13 !== e.tag;) e = e.return;
			Pa = e
		}

		function Ma(e) {
			if (e !== Pa) return !1;
			if (!Ca) return Ra(e), Ca = !0, !1;
			var t = e.type;
			if (5 !== e.tag || "head" !== t && "body" !== t && !Sr(t, e.memoizedProps))
				for (t = Da; t;) Aa(e, t), t = Pr(t.nextSibling);
			if (Ra(e), 13 === e.tag) {
				if (!(e = null !== (e = e.memoizedState) ? e.dehydrated : null)) throw Error(n(317));
				e: {
					for (e = e.nextSibling, t = 0; e;) {
						if (8 === e.nodeType) {
							var r = e.data;
							if (r === yr) {
								if (0 === t) {
									Da = Pr(e.nextSibling);
									break e
								}
								t--
							} else r !== br && r !== wr && r !== _r || t++
						}
						e = e.nextSibling
					}
					Da = null
				}
			} else Da = Pa ? Pr(e.stateNode.nextSibling) : null;
			return !0
		}

		function za() {
			Da = Pa = null, Ca = !1
		}
		var Fa = J.ReactCurrentOwner,
			ja = !1;

		function La(e, t, r, n) {
			t.child = null === e ? Ri(t, null, r, n) : Bi(t, e.child, r, n)
		}

		function Ua(e, t, r, n, o) {
			r = r.render;
			var i = t.ref;
			return di(t, o), n = ra(e, t, r, n, i, o), null === e || ja ? (t.effectTag |= 1, La(e, t, n, o), t.child) : (t.updateQueue = e.updateQueue, t.effectTag &= -517, e.expirationTime <= o && (e.expirationTime = 0), nc(e, t, o))
		}

		function Na(e, t, r, n, o, i) {
			if (null === e) {
				var a = r.type;
				return "function" != typeof a || qs(a) || void 0 !== a.defaultProps || null !== r.compare || void 0 !== r.defaultProps ? ((e = Gs(r.type, null, n, null, t.mode, i)).ref = t.ref, e.return = t, t.child = e) : (t.tag = 15, t.type = a, Ha(e, t, a, n, o, i))
			}
			return a = e.child, o < i && (o = a.memoizedProps, (r = null !== (r = r.compare) ? r : Kn)(o, n) && e.ref === t.ref) ? nc(e, t, i) : (t.effectTag |= 1, (e = Ws(a, n)).ref = t.ref, e.return = t, t.child = e)
		}

		function Ha(e, t, r, n, o, i) {
			return null !== e && Kn(e.memoizedProps, n) && e.ref === t.ref && (ja = !1, o < i) ? (t.expirationTime = e.expirationTime, nc(e, t, i)) : qa(e, t, r, n, i)
		}

		function Va(e, t) {
			var r = t.ref;
			(null === e && null !== r || null !== e && e.ref !== r) && (t.effectTag |= 128)
		}

		function qa(e, t, r, n, o) {
			var i = To(r) ? ko : _o.current;
			return i = xo(t, i), di(t, o), r = ra(e, t, r, n, i, o), null === e || ja ? (t.effectTag |= 1, La(e, t, r, o), t.child) : (t.updateQueue = e.updateQueue, t.effectTag &= -517, e.expirationTime <= o && (e.expirationTime = 0), nc(e, t, o))
		}

		function Wa(e, t, r, n, o) {
			if (To(r)) {
				var i = !0;
				Po(t)
			} else i = !1;
			if (di(t, o), null === t.stateNode) null !== e && (e.alternate = null, t.alternate = null, t.effectTag |= 2), Ii(t, r, n), Pi(t, r, n, o), n = !0;
			else if (null === e) {
				var a = t.stateNode,
					c = t.memoizedProps;
				a.props = c;
				var s = a.context,
					u = r.contextType;
				"object" == typeof u && null !== u ? u = pi(u) : u = xo(t, u = To(r) ? ko : _o.current);
				var l = r.getDerivedStateFromProps,
					d = "function" == typeof l || "function" == typeof a.getSnapshotBeforeUpdate;
				d || "function" != typeof a.UNSAFE_componentWillReceiveProps && "function" != typeof a.componentWillReceiveProps || (c !== n || s !== u) && Ei(t, a, n, u), fi = !1;
				var p = t.memoizedState;
				a.state = p, yi(t, n, a, o), s = t.memoizedState, c !== n || p !== s || wo.current || fi ? ("function" == typeof l && (xi(t, r, l, n), s = t.memoizedState), (c = fi || Si(t, r, c, n, p, s, u)) ? (d || "function" != typeof a.UNSAFE_componentWillMount && "function" != typeof a.componentWillMount || ("function" == typeof a.componentWillMount && a.componentWillMount(), "function" == typeof a.UNSAFE_componentWillMount && a.UNSAFE_componentWillMount()), "function" == typeof a.componentDidMount && (t.effectTag |= 4)) : ("function" == typeof a.componentDidMount && (t.effectTag |= 4), t.memoizedProps = n, t.memoizedState = s), a.props = n, a.state = s, a.context = u, n = c) : ("function" == typeof a.componentDidMount && (t.effectTag |= 4), n = !1)
			} else a = t.stateNode, gi(e, t), c = t.memoizedProps, a.props = t.type === t.elementType ? c : ni(t.type, c), s = a.context, "object" == typeof(u = r.contextType) && null !== u ? u = pi(u) : u = xo(t, u = To(r) ? ko : _o.current), (d = "function" == typeof(l = r.getDerivedStateFromProps) || "function" == typeof a.getSnapshotBeforeUpdate) || "function" != typeof a.UNSAFE_componentWillReceiveProps && "function" != typeof a.componentWillReceiveProps || (c !== n || s !== u) && Ei(t, a, n, u), fi = !1, s = t.memoizedState, a.state = s, yi(t, n, a, o), p = t.memoizedState, c !== n || s !== p || wo.current || fi ? ("function" == typeof l && (xi(t, r, l, n), p = t.memoizedState), (l = fi || Si(t, r, c, n, s, p, u)) ? (d || "function" != typeof a.UNSAFE_componentWillUpdate && "function" != typeof a.componentWillUpdate || ("function" == typeof a.componentWillUpdate && a.componentWillUpdate(n, p, u), "function" == typeof a.UNSAFE_componentWillUpdate && a.UNSAFE_componentWillUpdate(n, p, u)), "function" == typeof a.componentDidUpdate && (t.effectTag |= 4), "function" == typeof a.getSnapshotBeforeUpdate && (t.effectTag |= 256)) : ("function" != typeof a.componentDidUpdate || c === e.memoizedProps && s === e.memoizedState || (t.effectTag |= 4), "function" != typeof a.getSnapshotBeforeUpdate || c === e.memoizedProps && s === e.memoizedState || (t.effectTag |= 256), t.memoizedProps = n, t.memoizedState = p), a.props = n, a.state = p, a.context = u, n = l) : ("function" != typeof a.componentDidUpdate || c === e.memoizedProps && s === e.memoizedState || (t.effectTag |= 4), "function" != typeof a.getSnapshotBeforeUpdate || c === e.memoizedProps && s === e.memoizedState || (t.effectTag |= 256), n = !1);
			return Ga(e, t, r, n, i, o)
		}

		function Ga(e, t, r, n, o, i) {
			Va(e, t);
			var a = 0 != (64 & t.effectTag);
			if (!n && !a) return o && Do(t, r, !1), nc(e, t, i);
			n = t.stateNode, Fa.current = t;
			var c = a && "function" != typeof r.getDerivedStateFromError ? null : n.render();
			return t.effectTag |= 1, null !== e && a ? (t.child = Bi(t, e.child, null, i), t.child = Bi(t, null, c, i)) : La(e, t, c, i), t.memoizedState = n.state, o && Do(t, r, !0), t.child
		}

		function Ka(e) {
			var t = e.stateNode;
			t.pendingContext ? Io(0, t.pendingContext, t.pendingContext !== t.context) : t.context && Io(0, t.context, !1), Ui(e, t.containerInfo)
		}
		var $a, Qa, Xa, Ya, Ja = {
			dehydrated: null,
			retryTime: 0
		};

		function Za(e, t, r) {
			var n, o = t.mode,
				i = t.pendingProps,
				a = qi.current,
				c = !1;
			if ((n = 0 != (64 & t.effectTag)) || (n = 0 != (2 & a) && (null === e || null !== e.memoizedState)), n ? (c = !0, t.effectTag &= -65) : null !== e && null === e.memoizedState || void 0 === i.fallback || !0 === i.unstable_avoidThisFallback || (a |= 1), bo(qi, 1 & a), null === e) {
				if (void 0 !== i.fallback && Ba(t), c) {
					if (c = i.fallback, (i = Ks(null, o, 0, null)).return = t, 0 == (2 & t.mode))
						for (e = null !== t.memoizedState ? t.child.child : t.child, i.child = e; null !== e;) e.return = i, e = e.sibling;
					return (r = Ks(c, o, r, null)).return = t, i.sibling = r, t.memoizedState = Ja, t.child = i, r
				}
				return o = i.children, t.memoizedState = null, t.child = Ri(t, null, o, r)
			}
			if (null !== e.memoizedState) {
				if (o = (e = e.child).sibling, c) {
					if (i = i.fallback, (r = Ws(e, e.pendingProps)).return = t, 0 == (2 & t.mode) && (c = null !== t.memoizedState ? t.child.child : t.child) !== e.child)
						for (r.child = c; null !== c;) c.return = r, c = c.sibling;
					return (o = Ws(o, i)).return = t, r.sibling = o, r.childExpirationTime = 0, t.memoizedState = Ja, t.child = r, o
				}
				return r = Bi(t, e.child, i.children, r), t.memoizedState = null, t.child = r
			}
			if (e = e.child, c) {
				if (c = i.fallback, (i = Ks(null, o, 0, null)).return = t, i.child = e, null !== e && (e.return = i), 0 == (2 & t.mode))
					for (e = null !== t.memoizedState ? t.child.child : t.child, i.child = e; null !== e;) e.return = i, e = e.sibling;
				return (r = Ks(c, o, r, null)).return = t, i.sibling = r, r.effectTag |= 2, i.childExpirationTime = 0, t.memoizedState = Ja, t.child = i, r
			}
			return t.memoizedState = null, t.child = Bi(t, e, i.children, r)
		}

		function ec(e, t) {
			e.expirationTime < t && (e.expirationTime = t);
			var r = e.alternate;
			null !== r && r.expirationTime < t && (r.expirationTime = t), li(e.return, t)
		}

		function tc(e, t, r, n, o, i) {
			var a = e.memoizedState;
			null === a ? e.memoizedState = {
				isBackwards: t,
				rendering: null,
				renderingStartTime: 0,
				last: n,
				tail: r,
				tailExpiration: 0,
				tailMode: o,
				lastEffect: i
			} : (a.isBackwards = t, a.rendering = null, a.renderingStartTime = 0, a.last = n, a.tail = r, a.tailExpiration = 0, a.tailMode = o, a.lastEffect = i)
		}

		function rc(e, t, r) {
			var n = t.pendingProps,
				o = n.revealOrder,
				i = n.tail;
			if (La(e, t, n.children, r), 0 != (2 & (n = qi.current))) n = 1 & n | 2, t.effectTag |= 64;
			else {
				if (null !== e && 0 != (64 & e.effectTag)) e: for (e = t.child; null !== e;) {
					if (13 === e.tag) null !== e.memoizedState && ec(e, r);
					else if (19 === e.tag) ec(e, r);
					else if (null !== e.child) {
						e.child.return = e, e = e.child;
						continue
					}
					if (e === t) break e;
					for (; null === e.sibling;) {
						if (null === e.return || e.return === t) break e;
						e = e.return
					}
					e.sibling.return = e.return, e = e.sibling
				}
				n &= 1
			}
			if (bo(qi, n), 0 == (2 & t.mode)) t.memoizedState = null;
			else switch (o) {
				case "forwards":
					for (r = t.child, o = null; null !== r;) null !== (e = r.alternate) && null === Wi(e) && (o = r), r = r.sibling;
					null === (r = o) ? (o = t.child, t.child = null) : (o = r.sibling, r.sibling = null), tc(t, !1, o, r, i, t.lastEffect);
					break;
				case "backwards":
					for (r = null, o = t.child, t.child = null; null !== o;) {
						if (null !== (e = o.alternate) && null === Wi(e)) {
							t.child = o;
							break
						}
						e = o.sibling, o.sibling = r, r = o, o = e
					}
					tc(t, !0, r, null, i, t.lastEffect);
					break;
				case "together":
					tc(t, !1, null, null, void 0, t.lastEffect);
					break;
				default:
					t.memoizedState = null
			}
			return t.child
		}

		function nc(e, t, r) {
			null !== e && (t.dependencies = e.dependencies);
			var o = t.expirationTime;
			if (0 !== o && Ss(o), t.childExpirationTime < r) return null;
			if (null !== e && t.child !== e.child) throw Error(n(153));
			if (null !== t.child) {
				for (r = Ws(e = t.child, e.pendingProps), t.child = r, r.return = t; null !== e.sibling;) e = e.sibling, (r = r.sibling = Ws(e, e.pendingProps)).return = t;
				r.sibling = null
			}
			return t.child
		}

		function oc(e, t) {
			switch (e.tailMode) {
				case "hidden":
					t = e.tail;
					for (var r = null; null !== t;) null !== t.alternate && (r = t), t = t.sibling;
					null === r ? e.tail = null : r.sibling = null;
					break;
				case "collapsed":
					r = e.tail;
					for (var n = null; null !== r;) null !== r.alternate && (n = r), r = r.sibling;
					null === n ? t || null === e.tail ? e.tail = null : e.tail.sibling = null : n.sibling = null
			}
		}

		function ic(e, r, o) {
			var i = r.pendingProps;
			switch (r.tag) {
				case 2:
				case 16:
				case 15:
				case 0:
				case 11:
				case 7:
				case 8:
				case 12:
				case 9:
				case 14:
					return null;
				case 1:
				case 17:
					return To(r.type) && So(), null;
				case 3:
					return Ni(), vo(wo), vo(_o), (o = r.stateNode).pendingContext && (o.context = o.pendingContext, o.pendingContext = null), null !== e && null !== e.child || !Ma(r) || (r.effectTag |= 4), Qa(r), null;
				case 5:
					Vi(r), o = Li(ji.current);
					var a = r.type;
					if (null !== e && null != r.stateNode) Xa(e, r, a, i, o), e.ref !== r.ref && (r.effectTag |= 128);
					else {
						if (!i) {
							if (null === r.stateNode) throw Error(n(166));
							return null
						}
						if (e = Li(zi.current), Ma(r)) {
							i = r.stateNode, a = r.type;
							var c = r.memoizedProps;
							switch (i[Ar] = r, i[Or] = c, a) {
								case "iframe":
								case "object":
								case "embed":
									Xt("load", i);
									break;
								case "video":
								case "audio":
									for (e = 0; e < Ze.length; e++) Xt(Ze[e], i);
									break;
								case "source":
									Xt("error", i);
									break;
								case "img":
								case "image":
								case "link":
									Xt("error", i), Xt("load", i);
									break;
								case "form":
									Xt("reset", i), Xt("submit", i);
									break;
								case "details":
									Xt("toggle", i);
									break;
								case "input":
									Se(i, c), Xt("invalid", i), lr(o, "onChange");
									break;
								case "select":
									i._wrapperState = {
										wasMultiple: !!c.multiple
									}, Xt("invalid", i), lr(o, "onChange");
									break;
								case "textarea":
									Be(i, c), Xt("invalid", i), lr(o, "onChange")
							}
							for (var s in cr(a, c), e = null, c)
								if (c.hasOwnProperty(s)) {
									var u = c[s];
									"children" === s ? "string" == typeof u ? i.textContent !== u && (e = ["children", u]) : "number" == typeof u && i.textContent !== "" + u && (e = ["children", "" + u]) : I.hasOwnProperty(s) && null != u && lr(o, s)
								} switch (a) {
								case "input":
									ke(i), Pe(i, c, !0);
									break;
								case "textarea":
									ke(i), Me(i);
									break;
								case "select":
								case "option":
									break;
								default:
									"function" == typeof c.onClick && (i.onclick = dr)
							}
							o = e, r.updateQueue = o, null !== o && (r.effectTag |= 4)
						} else {
							switch (s = 9 === o.nodeType ? o : o.ownerDocument, e === ur && (e = je(a)), e === ur ? "script" === a ? ((e = s.createElement("div")).innerHTML = "<script><\/script>", e = e.removeChild(e.firstChild)) : "string" == typeof i.is ? e = s.createElement(a, {
								is: i.is
							}) : (e = s.createElement(a), "select" === a && (s = e, i.multiple ? s.multiple = !0 : i.size && (s.size = i.size))) : e = s.createElementNS(e, a), e[Ar] = r, e[Or] = i, $a(e, r, !1, !1), r.stateNode = e, s = sr(a, i), a) {
								case "iframe":
								case "object":
								case "embed":
									Xt("load", e), u = i;
									break;
								case "video":
								case "audio":
									for (u = 0; u < Ze.length; u++) Xt(Ze[u], e);
									u = i;
									break;
								case "source":
									Xt("error", e), u = i;
									break;
								case "img":
								case "image":
								case "link":
									Xt("error", e), Xt("load", e), u = i;
									break;
								case "form":
									Xt("reset", e), Xt("submit", e), u = i;
									break;
								case "details":
									Xt("toggle", e), u = i;
									break;
								case "input":
									Se(e, i), u = Te(e, i), Xt("invalid", e), lr(o, "onChange");
									break;
								case "option":
									u = Ce(e, i);
									break;
								case "select":
									e._wrapperState = {
										wasMultiple: !!i.multiple
									}, u = t({}, i, {
										value: void 0
									}), Xt("invalid", e), lr(o, "onChange");
									break;
								case "textarea":
									Be(e, i), u = Oe(e, i), Xt("invalid", e), lr(o, "onChange");
									break;
								default:
									u = i
							}
							cr(a, u);
							var l = u;
							for (c in l)
								if (l.hasOwnProperty(c)) {
									var d = l[c];
									"style" === c ? ir(e, d) : "dangerouslySetInnerHTML" === c ? null != (d = d ? d.__html : void 0) && He(e, d) : "children" === c ? "string" == typeof d ? ("textarea" !== a || "" !== d) && Ve(e, d) : "number" == typeof d && Ve(e, "" + d) : "suppressContentEditableWarning" !== c && "suppressHydrationWarning" !== c && "autoFocus" !== c && (I.hasOwnProperty(c) ? null != d && lr(o, c) : null != d && Z(e, c, d, s))
								} switch (a) {
								case "input":
									ke(e), Pe(e, i, !1);
									break;
								case "textarea":
									ke(e), Me(e);
									break;
								case "option":
									null != i.value && e.setAttribute("value", "" + _e(i.value));
									break;
								case "select":
									e.multiple = !!i.multiple, null != (o = i.value) ? Ae(e, !!i.multiple, o, !1) : null != i.defaultValue && Ae(e, !!i.multiple, i.defaultValue, !0);
									break;
								default:
									"function" == typeof u.onClick && (e.onclick = dr)
							}
							Tr(a, i) && (r.effectTag |= 4)
						}
						null !== r.ref && (r.effectTag |= 128)
					}
					return null;
				case 6:
					if (e && null != r.stateNode) Ya(e, r, e.memoizedProps, i);
					else {
						if ("string" != typeof i && null === r.stateNode) throw Error(n(166));
						o = Li(ji.current), Li(zi.current), Ma(r) ? (o = r.stateNode, i = r.memoizedProps, o[Ar] = r, o.nodeValue !== i && (r.effectTag |= 4)) : ((o = (9 === o.nodeType ? o : o.ownerDocument).createTextNode(i))[Ar] = r, r.stateNode = o)
					}
					return null;
				case 13:
					return vo(qi), i = r.memoizedState, 0 != (64 & r.effectTag) ? (r.expirationTime = o, r) : (o = null !== i, i = !1, null === e ? void 0 !== r.memoizedProps.fallback && Ma(r) : (i = null !== (a = e.memoizedState), o || null === a || null !== (a = e.child.sibling) && (null !== (c = r.firstEffect) ? (r.firstEffect = a, a.nextEffect = c) : (r.firstEffect = r.lastEffect = a, a.nextEffect = null), a.effectTag = 8)), o && !i && 0 != (2 & r.mode) && (null === e && !0 !== r.memoizedProps.unstable_avoidThisFallback || 0 != (1 & qi.current) ? Wc === Mc && (Wc = jc) : (Wc !== Mc && Wc !== jc || (Wc = Lc), 0 !== Xc && null !== Hc && (Js(Hc, qc), Zs(Hc, Xc)))), (o || i) && (r.effectTag |= 4), null);
				case 4:
					return Ni(), Qa(r), null;
				case 10:
					return ui(r), null;
				case 19:
					if (vo(qi), null === (i = r.memoizedState)) return null;
					if (a = 0 != (64 & r.effectTag), null === (c = i.rendering)) {
						if (a) oc(i, !1);
						else if (Wc !== Mc || null !== e && 0 != (64 & e.effectTag))
							for (c = r.child; null !== c;) {
								if (null !== (e = Wi(c))) {
									for (r.effectTag |= 64, oc(i, !1), null !== (a = e.updateQueue) && (r.updateQueue = a, r.effectTag |= 4), null === i.lastEffect && (r.firstEffect = null), r.lastEffect = i.lastEffect, i = r.child; null !== i;) c = o, (a = i).effectTag &= 2, a.nextEffect = null, a.firstEffect = null, a.lastEffect = null, null === (e = a.alternate) ? (a.childExpirationTime = 0, a.expirationTime = c, a.child = null, a.memoizedProps = null, a.memoizedState = null, a.updateQueue = null, a.dependencies = null) : (a.childExpirationTime = e.childExpirationTime, a.expirationTime = e.expirationTime, a.child = e.child, a.memoizedProps = e.memoizedProps, a.memoizedState = e.memoizedState, a.updateQueue = e.updateQueue, c = e.dependencies, a.dependencies = null === c ? null : {
										expirationTime: c.expirationTime,
										firstContext: c.firstContext,
										responders: c.responders
									}), i = i.sibling;
									return bo(qi, 1 & qi.current | 2), r.child
								}
								c = c.sibling
							}
					} else {
						if (!a)
							if (null !== (e = Wi(c))) {
								if (r.effectTag |= 64, a = !0, null !== (o = e.updateQueue) && (r.updateQueue = o, r.effectTag |= 4), oc(i, !0), null === i.tail && "hidden" === i.tailMode && !c.alternate) return null !== (r = r.lastEffect = i.lastEffect) && (r.nextEffect = null), null
							} else 2 * $o() - i.renderingStartTime > i.tailExpiration && 1 < o && (r.effectTag |= 64, a = !0, oc(i, !1), r.expirationTime = r.childExpirationTime = o - 1);
						i.isBackwards ? (c.sibling = r.child, r.child = c) : (null !== (o = i.last) ? o.sibling = c : r.child = c, i.last = c)
					}
					return null !== i.tail ? (0 === i.tailExpiration && (i.tailExpiration = $o() + 500), o = i.tail, i.rendering = o, i.tail = o.sibling, i.lastEffect = r.lastEffect, i.renderingStartTime = $o(), o.sibling = null, r = qi.current, bo(qi, a ? 1 & r | 2 : 1 & r), o) : null
			}
			throw Error(n(156, r.tag))
		}

		function ac(e) {
			switch (e.tag) {
				case 1:
					To(e.type) && So();
					var t = e.effectTag;
					return 4096 & t ? (e.effectTag = -4097 & t | 64, e) : null;
				case 3:
					if (Ni(), vo(wo), vo(_o), 0 != (64 & (t = e.effectTag))) throw Error(n(285));
					return e.effectTag = -4097 & t | 64, e;
				case 5:
					return Vi(e), null;
				case 13:
					return vo(qi), 4096 & (t = e.effectTag) ? (e.effectTag = -4097 & t | 64, e) : null;
				case 19:
					return vo(qi), null;
				case 4:
					return Ni(), null;
				case 10:
					return ui(e), null;
				default:
					return null
			}
		}

		function cc(e, t) {
			return {
				value: e,
				source: t,
				stack: ye(t)
			}
		}
		$a = function(e, t) {
			for (var r = t.child; null !== r;) {
				if (5 === r.tag || 6 === r.tag) e.appendChild(r.stateNode);
				else if (4 !== r.tag && null !== r.child) {
					r.child.return = r, r = r.child;
					continue
				}
				if (r === t) break;
				for (; null === r.sibling;) {
					if (null === r.return || r.return === t) return;
					r = r.return
				}
				r.sibling.return = r.return, r = r.sibling
			}
		}, Qa = function() {}, Xa = function(e, r, n, o, i) {
			var a = e.memoizedProps;
			if (a !== o) {
				var c, s, u = r.stateNode;
				switch (Li(zi.current), e = null, n) {
					case "input":
						a = Te(u, a), o = Te(u, o), e = [];
						break;
					case "option":
						a = Ce(u, a), o = Ce(u, o), e = [];
						break;
					case "select":
						a = t({}, a, {
							value: void 0
						}), o = t({}, o, {
							value: void 0
						}), e = [];
						break;
					case "textarea":
						a = Oe(u, a), o = Oe(u, o), e = [];
						break;
					default:
						"function" != typeof a.onClick && "function" == typeof o.onClick && (u.onclick = dr)
				}
				for (c in cr(n, o), n = null, a)
					if (!o.hasOwnProperty(c) && a.hasOwnProperty(c) && null != a[c])
						if ("style" === c)
							for (s in u = a[c]) u.hasOwnProperty(s) && (n || (n = {}), n[s] = "");
						else "dangerouslySetInnerHTML" !== c && "children" !== c && "suppressContentEditableWarning" !== c && "suppressHydrationWarning" !== c && "autoFocus" !== c && (I.hasOwnProperty(c) ? e || (e = []) : (e = e || []).push(c, null));
				for (c in o) {
					var l = o[c];
					if (u = null != a ? a[c] : void 0, o.hasOwnProperty(c) && l !== u && (null != l || null != u))
						if ("style" === c)
							if (u) {
								for (s in u) !u.hasOwnProperty(s) || l && l.hasOwnProperty(s) || (n || (n = {}), n[s] = "");
								for (s in l) l.hasOwnProperty(s) && u[s] !== l[s] && (n || (n = {}), n[s] = l[s])
							} else n || (e || (e = []), e.push(c, n)), n = l;
					else "dangerouslySetInnerHTML" === c ? (l = l ? l.__html : void 0, u = u ? u.__html : void 0, null != l && u !== l && (e = e || []).push(c, l)) : "children" === c ? u === l || "string" != typeof l && "number" != typeof l || (e = e || []).push(c, "" + l) : "suppressContentEditableWarning" !== c && "suppressHydrationWarning" !== c && (I.hasOwnProperty(c) ? (null != l && lr(i, c), e || u === l || (e = [])) : (e = e || []).push(c, l))
				}
				n && (e = e || []).push("style", n), i = e, (r.updateQueue = i) && (r.effectTag |= 4)
			}
		}, Ya = function(e, t, r, n) {
			r !== n && (t.effectTag |= 4)
		};
		var sc = "function" == typeof WeakSet ? WeakSet : Set;

		function uc(e, t) {
			var r = t.source,
				n = t.stack;
			null === n && null !== r && (n = ye(r)), null !== r && be(r.type), t = t.value, null !== e && 1 === e.tag && be(e.type);
			try {
				console.error(t)
			} catch (e) {
				setTimeout((function() {
					throw e
				}))
			}
		}

		function lc(e) {
			var t = e.ref;
			if (null !== t)
				if ("function" == typeof t) try {
					t(null)
				} catch (t) {
					Fs(e, t)
				} else t.current = null
		}

		function dc(e, t) {
			switch (t.tag) {
				case 0:
				case 11:
				case 15:
				case 22:
				case 3:
				case 5:
				case 6:
				case 4:
				case 17:
					return;
				case 1:
					if (256 & t.effectTag && null !== e) {
						var r = e.memoizedProps,
							o = e.memoizedState;
						t = (e = t.stateNode).getSnapshotBeforeUpdate(t.elementType === t.type ? r : ni(t.type, r), o), e.__reactInternalSnapshotBeforeUpdate = t
					}
					return
			}
			throw Error(n(163))
		}

		function pc(e, t) {
			if (null !== (t = null !== (t = t.updateQueue) ? t.lastEffect : null)) {
				var r = t = t.next;
				do {
					if ((r.tag & e) === e) {
						var n = r.destroy;
						r.destroy = void 0, void 0 !== n && n()
					}
					r = r.next
				} while (r !== t)
			}
		}

		function fc(e, t) {
			if (null !== (t = null !== (t = t.updateQueue) ? t.lastEffect : null)) {
				var r = t = t.next;
				do {
					if ((r.tag & e) === e) {
						var n = r.create;
						r.destroy = n()
					}
					r = r.next
				} while (r !== t)
			}
		}

		function mc(e, t, r) {
			switch (r.tag) {
				case 0:
				case 11:
				case 15:
				case 22:
					return void fc(3, r);
				case 1:
					if (e = r.stateNode, 4 & r.effectTag)
						if (null === t) e.componentDidMount();
						else {
							var o = r.elementType === r.type ? t.memoizedProps : ni(r.type, t.memoizedProps);
							e.componentDidUpdate(o, t.memoizedState, e.__reactInternalSnapshotBeforeUpdate)
						} return void(null !== (t = r.updateQueue) && _i(r, t, e));
				case 3:
					if (null !== (t = r.updateQueue)) {
						if (e = null, null !== r.child) switch (r.child.tag) {
							case 5:
							case 1:
								e = r.child.stateNode
						}
						_i(r, t, e)
					}
					return;
				case 5:
					return e = r.stateNode, void(null === t && 4 & r.effectTag && Tr(r.type, r.memoizedProps) && e.focus());
				case 6:
				case 4:
				case 12:
				case 19:
				case 17:
				case 20:
				case 21:
					return;
				case 13:
					return void(null === r.memoizedState && (r = r.alternate, null !== r && (r = r.memoizedState, null !== r && (r = r.dehydrated, null !== r && Lt(r)))))
			}
			throw Error(n(163))
		}

		function gc(e, t, r) {
			switch ("function" == typeof Ns && Ns(t), t.tag) {
				case 0:
				case 11:
				case 14:
				case 15:
				case 22:
					if (null !== (e = t.updateQueue) && null !== (e = e.lastEffect)) {
						var n = e.next;
						Yo(97 < r ? 97 : r, (function() {
							var e = n;
							do {
								var r = e.destroy;
								if (void 0 !== r) {
									var o = t;
									try {
										r()
									} catch (e) {
										Fs(o, e)
									}
								}
								e = e.next
							} while (e !== n)
						}))
					}
					break;
				case 1:
					lc(t), "function" == typeof(r = t.stateNode).componentWillUnmount && function(e, t) {
						try {
							t.props = e.memoizedProps, t.state = e.memoizedState, t.componentWillUnmount()
						} catch (t) {
							Fs(e, t)
						}
					}(t, r);
					break;
				case 5:
					lc(t);
					break;
				case 4:
					wc(e, t, r)
			}
		}

		function hc(e) {
			var t = e.alternate;
			e.return = null, e.child = null, e.memoizedState = null, e.updateQueue = null, e.dependencies = null, e.alternate = null, e.firstEffect = null, e.lastEffect = null, e.pendingProps = null, e.memoizedProps = null, e.stateNode = null, null !== t && hc(t)
		}

		function vc(e) {
			return 5 === e.tag || 3 === e.tag || 4 === e.tag
		}

		function bc(e) {
			e: {
				for (var t = e.return; null !== t;) {
					if (vc(t)) {
						var r = t;
						break e
					}
					t = t.return
				}
				throw Error(n(160))
			}
			switch (t = r.stateNode, r.tag) {
				case 5:
					var o = !1;
					break;
				case 3:
				case 4:
					t = t.containerInfo, o = !0;
					break;
				default:
					throw Error(n(161))
			}
			16 & r.effectTag && (Ve(t, ""), r.effectTag &= -17);e: t: for (r = e;;) {
				for (; null === r.sibling;) {
					if (null === r.return || vc(r.return)) {
						r = null;
						break e
					}
					r = r.return
				}
				for (r.sibling.return = r.return, r = r.sibling; 5 !== r.tag && 6 !== r.tag && 18 !== r.tag;) {
					if (2 & r.effectTag) continue t;
					if (null === r.child || 4 === r.tag) continue t;
					r.child.return = r, r = r.child
				}
				if (!(2 & r.effectTag)) {
					r = r.stateNode;
					break e
				}
			}
			o ? yc(e, r, t) : _c(e, r, t)
		}

		function yc(e, t, r) {
			var n = e.tag,
				o = 5 === n || 6 === n;
			if (o) e = o ? e.stateNode : e.stateNode.instance, t ? 8 === r.nodeType ? r.parentNode.insertBefore(e, t) : r.insertBefore(e, t) : (8 === r.nodeType ? (t = r.parentNode).insertBefore(e, r) : (t = r).appendChild(e), null != (r = r._reactRootContainer) || null !== t.onclick || (t.onclick = dr));
			else if (4 !== n && null !== (e = e.child))
				for (yc(e, t, r), e = e.sibling; null !== e;) yc(e, t, r), e = e.sibling
		}

		function _c(e, t, r) {
			var n = e.tag,
				o = 5 === n || 6 === n;
			if (o) e = o ? e.stateNode : e.stateNode.instance, t ? r.insertBefore(e, t) : r.appendChild(e);
			else if (4 !== n && null !== (e = e.child))
				for (_c(e, t, r), e = e.sibling; null !== e;) _c(e, t, r), e = e.sibling
		}

		function wc(e, t, r) {
			for (var o, i, a = t, c = !1;;) {
				if (!c) {
					c = a.return;
					e: for (;;) {
						if (null === c) throw Error(n(160));
						switch (o = c.stateNode, c.tag) {
							case 5:
								i = !1;
								break e;
							case 3:
							case 4:
								o = o.containerInfo, i = !0;
								break e
						}
						c = c.return
					}
					c = !0
				}
				if (5 === a.tag || 6 === a.tag) {
					e: for (var s = e, u = a, l = r, d = u;;)
						if (gc(s, d, l), null !== d.child && 4 !== d.tag) d.child.return = d, d = d.child;
						else {
							if (d === u) break e;
							for (; null === d.sibling;) {
								if (null === d.return || d.return === u) break e;
								d = d.return
							}
							d.sibling.return = d.return, d = d.sibling
						}i ? (s = o, u = a.stateNode, 8 === s.nodeType ? s.parentNode.removeChild(u) : s.removeChild(u)) : o.removeChild(a.stateNode)
				}
				else if (4 === a.tag) {
					if (null !== a.child) {
						o = a.stateNode.containerInfo, i = !0, a.child.return = a, a = a.child;
						continue
					}
				} else if (gc(e, a, r), null !== a.child) {
					a.child.return = a, a = a.child;
					continue
				}
				if (a === t) break;
				for (; null === a.sibling;) {
					if (null === a.return || a.return === t) return;
					4 === (a = a.return).tag && (c = !1)
				}
				a.sibling.return = a.return, a = a.sibling
			}
		}

		function kc(e, t) {
			switch (t.tag) {
				case 0:
				case 11:
				case 14:
				case 15:
				case 22:
					return void pc(3, t);
				case 1:
				case 12:
				case 17:
					return;
				case 5:
					var r = t.stateNode;
					if (null != r) {
						var o = t.memoizedProps,
							i = null !== e ? e.memoizedProps : o;
						e = t.type;
						var a = t.updateQueue;
						if (t.updateQueue = null, null !== a) {
							for (r[Or] = o, "input" === e && "radio" === o.type && null != o.name && Ie(r, o), sr(e, i), t = sr(e, o), i = 0; i < a.length; i += 2) {
								var c = a[i],
									s = a[i + 1];
								"style" === c ? ir(r, s) : "dangerouslySetInnerHTML" === c ? He(r, s) : "children" === c ? Ve(r, s) : Z(r, c, s, t)
							}
							switch (e) {
								case "input":
									Ee(r, o);
									break;
								case "textarea":
									Re(r, o);
									break;
								case "select":
									t = r._wrapperState.wasMultiple, r._wrapperState.wasMultiple = !!o.multiple, null != (e = o.value) ? Ae(r, !!o.multiple, e, !1) : t !== !!o.multiple && (null != o.defaultValue ? Ae(r, !!o.multiple, o.defaultValue, !0) : Ae(r, !!o.multiple, o.multiple ? [] : "", !1))
							}
						}
					}
					return;
				case 6:
					if (null === t.stateNode) throw Error(n(162));
					return void(t.stateNode.nodeValue = t.memoizedProps);
				case 3:
					return void((t = t.stateNode).hydrate && (t.hydrate = !1, Lt(t.containerInfo)));
				case 13:
					if (r = t, null === t.memoizedState ? o = !1 : (o = !0, r = t.child, Jc = $o()), null !== r) e: for (e = r;;) {
						if (5 === e.tag) a = e.stateNode, o ? "function" == typeof(a = a.style).setProperty ? a.setProperty("display", "none", "important") : a.display = "none" : (a = e.stateNode, i = null != (i = e.memoizedProps.style) && i.hasOwnProperty("display") ? i.display : null, a.style.display = or("display", i));
						else if (6 === e.tag) e.stateNode.nodeValue = o ? "" : e.memoizedProps;
						else {
							if (13 === e.tag && null !== e.memoizedState && null === e.memoizedState.dehydrated) {
								(a = e.child.sibling).return = e, e = a;
								continue
							}
							if (null !== e.child) {
								e.child.return = e, e = e.child;
								continue
							}
						}
						if (e === r) break;
						for (; null === e.sibling;) {
							if (null === e.return || e.return === r) break e;
							e = e.return
						}
						e.sibling.return = e.return, e = e.sibling
					}
					return void xc(t);
				case 19:
					return void xc(t)
			}
			throw Error(n(163))
		}

		function xc(e) {
			var t = e.updateQueue;
			if (null !== t) {
				e.updateQueue = null;
				var r = e.stateNode;
				null === r && (r = e.stateNode = new sc), t.forEach((function(t) {
					var n = Ls.bind(null, e, t);
					r.has(t) || (r.add(t), t.then(n, n))
				}))
			}
		}
		var Tc = "function" == typeof WeakMap ? WeakMap : Map;

		function Sc(e, t, r) {
			(r = hi(r, null)).tag = 3, r.payload = {
				element: null
			};
			var n = t.value;
			return r.callback = function() {
				ts || (ts = !0, rs = n), uc(e, t)
			}, r
		}

		function Ic(e, t, r) {
			(r = hi(r, null)).tag = 3;
			var n = e.type.getDerivedStateFromError;
			if ("function" == typeof n) {
				var o = t.value;
				r.payload = function() {
					return uc(e, t), n(o)
				}
			}
			var i = e.stateNode;
			return null !== i && "function" == typeof i.componentDidCatch && (r.callback = function() {
				"function" != typeof n && (null === ns ? ns = new Set([this]) : ns.add(this), uc(e, t));
				var r = t.stack;
				this.componentDidCatch(t.value, {
					componentStack: null !== r ? r : ""
				})
			}), r
		}
		var Ec, Pc = Math.ceil,
			Dc = J.ReactCurrentDispatcher,
			Cc = J.ReactCurrentOwner,
			Ac = 0,
			Oc = 8,
			Bc = 16,
			Rc = 32,
			Mc = 0,
			zc = 1,
			Fc = 2,
			jc = 3,
			Lc = 4,
			Uc = 5,
			Nc = Ac,
			Hc = null,
			Vc = null,
			qc = 0,
			Wc = Mc,
			Gc = null,
			Kc = 1073741823,
			$c = 1073741823,
			Qc = null,
			Xc = 0,
			Yc = !1,
			Jc = 0,
			Zc = 500,
			es = null,
			ts = !1,
			rs = null,
			ns = null,
			os = !1,
			is = null,
			as = 90,
			cs = null,
			ss = 0,
			us = null,
			ls = 0;

		function ds() {
			return (Nc & (Bc | Rc)) !== Ac ? 1073741821 - ($o() / 10 | 0) : 0 !== ls ? ls : ls = 1073741821 - ($o() / 10 | 0)
		}

		function ps(e, t, r) {
			if (0 == (2 & (t = t.mode))) return 1073741823;
			var o = Qo();
			if (0 == (4 & t)) return 99 === o ? 1073741823 : 1073741822;
			if ((Nc & Bc) !== Ac) return qc;
			if (null !== r) e = ri(e, 0 | r.timeoutMs || 5e3, 250);
			else switch (o) {
				case 99:
					e = 1073741823;
					break;
				case 98:
					e = ri(e, 150, 100);
					break;
				case 97:
				case 96:
					e = ri(e, 5e3, 250);
					break;
				case 95:
					e = 2;
					break;
				default:
					throw Error(n(326))
			}
			return null !== Hc && e === qc && --e, e
		}

		function fs(e, t) {
			if (50 < ss) throw ss = 0, us = null, Error(n(185));
			if (null !== (e = ms(e, t))) {
				var r = Qo();
				1073741823 === t ? (Nc & Oc) !== Ac && (Nc & (Bc | Rc)) === Ac ? bs(e) : (hs(e), Nc === Ac && ei()) : hs(e), (4 & Nc) === Ac || 98 !== r && 99 !== r || (null === cs ? cs = new Map([
					[e, t]
				]) : (void 0 === (r = cs.get(e)) || r > t) && cs.set(e, t))
			}
		}

		function ms(e, t) {
			e.expirationTime < t && (e.expirationTime = t);
			var r = e.alternate;
			null !== r && r.expirationTime < t && (r.expirationTime = t);
			var n = e.return,
				o = null;
			if (null === n && 3 === e.tag) o = e.stateNode;
			else
				for (; null !== n;) {
					if (r = n.alternate, n.childExpirationTime < t && (n.childExpirationTime = t), null !== r && r.childExpirationTime < t && (r.childExpirationTime = t), null === n.return && 3 === n.tag) {
						o = n.stateNode;
						break
					}
					n = n.return
				}
			return null !== o && (Hc === o && (Ss(t), Wc === Lc && Js(o, qc)), Zs(o, t)), o
		}

		function gs(e) {
			var t = e.lastExpiredTime;
			if (0 !== t) return t;
			if (!Ys(e, t = e.firstPendingTime)) return t;
			var r = e.lastPingedTime;
			return 2 >= (e = r > (e = e.nextKnownPendingLevel) ? r : e) && t !== e ? 0 : e
		}

		function hs(e) {
			if (0 !== e.lastExpiredTime) e.callbackExpirationTime = 1073741823, e.callbackPriority = 99, e.callbackNode = Zo(bs.bind(null, e));
			else {
				var t = gs(e),
					r = e.callbackNode;
				if (0 === t) null !== r && (e.callbackNode = null, e.callbackExpirationTime = 0, e.callbackPriority = 90);
				else {
					var n = ds();
					if (1073741823 === t ? n = 99 : 1 === t || 2 === t ? n = 95 : n = 0 >= (n = 10 * (1073741821 - t) - 10 * (1073741821 - n)) ? 99 : 250 >= n ? 98 : 5250 >= n ? 97 : 95, null !== r) {
						var o = e.callbackPriority;
						if (e.callbackExpirationTime === t && o >= n) return;
						r !== No && Oo(r)
					}
					e.callbackExpirationTime = t, e.callbackPriority = n, t = 1073741823 === t ? Zo(bs.bind(null, e)) : Jo(n, vs.bind(null, e), {
						timeout: 10 * (1073741821 - t) - $o()
					}), e.callbackNode = t
				}
			}
		}

		function vs(e, t) {
			if (ls = 0, t) return eu(e, t = ds()), hs(e), null;
			var r = gs(e);
			if (0 !== r) {
				if (t = e.callbackNode, (Nc & (Bc | Rc)) !== Ac) throw Error(n(327));
				if (Rs(), e === Hc && r === qc || ws(e, r), null !== Vc) {
					var o = Nc;
					Nc |= Bc;
					for (var i = xs();;) try {
						Es();
						break
					} catch (t) {
						ks(e, t)
					}
					if (si(), Nc = o, Dc.current = i, Wc === zc) throw t = Gc, ws(e, r), Js(e, r), hs(e), t;
					if (null === Vc) switch (i = e.finishedWork = e.current.alternate, e.finishedExpirationTime = r, o = Wc, Hc = null, o) {
						case Mc:
						case zc:
							throw Error(n(345));
						case Fc:
							eu(e, 2 < r ? 2 : r);
							break;
						case jc:
							if (Js(e, r), r === (o = e.lastSuspendedTime) && (e.nextKnownPendingLevel = Cs(i)), 1073741823 === Kc && 10 < (i = Jc + Zc - $o())) {
								if (Yc) {
									var a = e.lastPingedTime;
									if (0 === a || a >= r) {
										e.lastPingedTime = r, ws(e, r);
										break
									}
								}
								if (0 !== (a = gs(e)) && a !== r) break;
								if (0 !== o && o !== r) {
									e.lastPingedTime = o;
									break
								}
								e.timeoutHandle = Ir(As.bind(null, e), i);
								break
							}
							As(e);
							break;
						case Lc:
							if (Js(e, r), r === (o = e.lastSuspendedTime) && (e.nextKnownPendingLevel = Cs(i)), Yc && (0 === (i = e.lastPingedTime) || i >= r)) {
								e.lastPingedTime = r, ws(e, r);
								break
							}
							if (0 !== (i = gs(e)) && i !== r) break;
							if (0 !== o && o !== r) {
								e.lastPingedTime = o;
								break
							}
							if (1073741823 !== $c ? o = 10 * (1073741821 - $c) - $o() : 1073741823 === Kc ? o = 0 : (o = 10 * (1073741821 - Kc) - 5e3, 0 > (o = (i = $o()) - o) && (o = 0), (r = 10 * (1073741821 - r) - i) < (o = (120 > o ? 120 : 480 > o ? 480 : 1080 > o ? 1080 : 1920 > o ? 1920 : 3e3 > o ? 3e3 : 4320 > o ? 4320 : 1960 * Pc(o / 1960)) - o) && (o = r)), 10 < o) {
								e.timeoutHandle = Ir(As.bind(null, e), o);
								break
							}
							As(e);
							break;
						case Uc:
							if (1073741823 !== Kc && null !== Qc) {
								a = Kc;
								var c = Qc;
								if (0 >= (o = 0 | c.busyMinDurationMs) ? o = 0 : (i = 0 | c.busyDelayMs, o = (a = $o() - (10 * (1073741821 - a) - (0 | c.timeoutMs || 5e3))) <= i ? 0 : i + o - a), 10 < o) {
									Js(e, r), e.timeoutHandle = Ir(As.bind(null, e), o);
									break
								}
							}
							As(e);
							break;
						default:
							throw Error(n(329))
					}
					if (hs(e), e.callbackNode === t) return vs.bind(null, e)
				}
			}
			return null
		}

		function bs(e) {
			var t = e.lastExpiredTime;
			if (t = 0 !== t ? t : 1073741823, (Nc & (Bc | Rc)) !== Ac) throw Error(n(327));
			if (Rs(), e === Hc && t === qc || ws(e, t), null !== Vc) {
				var r = Nc;
				Nc |= Bc;
				for (var o = xs();;) try {
					Is();
					break
				} catch (t) {
					ks(e, t)
				}
				if (si(), Nc = r, Dc.current = o, Wc === zc) throw r = Gc, ws(e, t), Js(e, t), hs(e), r;
				if (null !== Vc) throw Error(n(261));
				e.finishedWork = e.current.alternate, e.finishedExpirationTime = t, Hc = null, As(e), hs(e)
			}
			return null
		}

		function ys(e, t) {
			var r = Nc;
			Nc |= 1;
			try {
				return e(t)
			} finally {
				(Nc = r) === Ac && ei()
			}
		}

		function _s(e, t) {
			var r = Nc;
			Nc &= -2, Nc |= Oc;
			try {
				return e(t)
			} finally {
				(Nc = r) === Ac && ei()
			}
		}

		function ws(e, t) {
			e.finishedWork = null, e.finishedExpirationTime = 0;
			var r = e.timeoutHandle;
			if (-1 !== r && (e.timeoutHandle = -1, Er(r)), null !== Vc)
				for (r = Vc.return; null !== r;) {
					var n = r;
					switch (n.tag) {
						case 1:
							null != (n = n.type.childContextTypes) && So();
							break;
						case 3:
							Ni(), vo(wo), vo(_o);
							break;
						case 5:
							Vi(n);
							break;
						case 4:
							Ni();
							break;
						case 13:
						case 19:
							vo(qi);
							break;
						case 10:
							ui(n)
					}
					r = r.return
				}
			Hc = e, Vc = Ws(e.current, null), qc = t, Wc = Mc, Gc = null, $c = Kc = 1073741823, Qc = null, Xc = 0, Yc = !1
		}

		function ks(e, t) {
			for (;;) {
				try {
					if (si(), Ki.current = Ta, Zi)
						for (var r = Xi.memoizedState; null !== r;) {
							var n = r.queue;
							null !== n && (n.pending = null), r = r.next
						}
					if (Qi = 0, Ji = Yi = Xi = null, Zi = !1, null === Vc || null === Vc.return) return Wc = zc, Gc = t, Vc = null;
					e: {
						var o = e,
							i = Vc.return,
							a = Vc,
							c = t;
						if (t = qc, a.effectTag |= 2048, a.firstEffect = a.lastEffect = null, null !== c && "object" == typeof c && "function" == typeof c.then) {
							var s = c;
							if (0 == (2 & a.mode)) {
								var u = a.alternate;
								u ? (a.updateQueue = u.updateQueue, a.memoizedState = u.memoizedState, a.expirationTime = u.expirationTime) : (a.updateQueue = null, a.memoizedState = null)
							}
							var l = 0 != (1 & qi.current),
								d = i;
							do {
								var p;
								if (p = 13 === d.tag) {
									var f = d.memoizedState;
									if (null !== f) p = null !== f.dehydrated;
									else {
										var m = d.memoizedProps;
										p = void 0 !== m.fallback && (!0 !== m.unstable_avoidThisFallback || !l)
									}
								}
								if (p) {
									var g = d.updateQueue;
									if (null === g) {
										var h = new Set;
										h.add(s), d.updateQueue = h
									} else g.add(s);
									if (0 == (2 & d.mode)) {
										if (d.effectTag |= 64, a.effectTag &= -2981, 1 === a.tag)
											if (null === a.alternate) a.tag = 17;
											else {
												var v = hi(1073741823, null);
												v.tag = 2, vi(a, v)
											} a.expirationTime = 1073741823;
										break e
									}
									c = void 0, a = t;
									var b = o.pingCache;
									if (null === b ? (b = o.pingCache = new Tc, c = new Set, b.set(s, c)) : void 0 === (c = b.get(s)) && (c = new Set, b.set(s, c)), !c.has(a)) {
										c.add(a);
										var y = js.bind(null, o, s, a);
										s.then(y, y)
									}
									d.effectTag |= 4096, d.expirationTime = t;
									break e
								}
								d = d.return
							} while (null !== d);
							c = Error((be(a.type) || "A React component") + " suspended while rendering, but no fallback UI was specified.\n\nAdd a <Suspense fallback=...> component higher in the tree to provide a loading indicator or placeholder to display." + ye(a))
						}
						Wc !== Uc && (Wc = Fc),
						c = cc(c, a),
						d = i;do {
							switch (d.tag) {
								case 3:
									s = c, d.effectTag |= 4096, d.expirationTime = t, bi(d, Sc(d, s, t));
									break e;
								case 1:
									s = c;
									var _ = d.type,
										w = d.stateNode;
									if (0 == (64 & d.effectTag) && ("function" == typeof _.getDerivedStateFromError || null !== w && "function" == typeof w.componentDidCatch && (null === ns || !ns.has(w)))) {
										d.effectTag |= 4096, d.expirationTime = t, bi(d, Ic(d, s, t));
										break e
									}
							}
							d = d.return
						} while (null !== d)
					}
					Vc = Ds(Vc)
				} catch (e) {
					t = e;
					continue
				}
				break
			}
		}

		function xs() {
			var e = Dc.current;
			return Dc.current = Ta, null === e ? Ta : e
		}

		function Ts(e, t) {
			e < Kc && 2 < e && (Kc = e), null !== t && e < $c && 2 < e && ($c = e, Qc = t)
		}

		function Ss(e) {
			e > Xc && (Xc = e)
		}

		function Is() {
			for (; null !== Vc;) Vc = Ps(Vc)
		}

		function Es() {
			for (; null !== Vc && !Ho();) Vc = Ps(Vc)
		}

		function Ps(e) {
			var t = Ec(e.alternate, e, qc);
			return e.memoizedProps = e.pendingProps, null === t && (t = Ds(e)), Cc.current = null, t
		}

		function Ds(e) {
			Vc = e;
			do {
				var t = Vc.alternate;
				if (e = Vc.return, 0 == (2048 & Vc.effectTag)) {
					if (t = ic(t, Vc, qc), 1 === qc || 1 !== Vc.childExpirationTime) {
						for (var r = 0, n = Vc.child; null !== n;) {
							var o = n.expirationTime,
								i = n.childExpirationTime;
							o > r && (r = o), i > r && (r = i), n = n.sibling
						}
						Vc.childExpirationTime = r
					}
					if (null !== t) return t;
					null !== e && 0 == (2048 & e.effectTag) && (null === e.firstEffect && (e.firstEffect = Vc.firstEffect), null !== Vc.lastEffect && (null !== e.lastEffect && (e.lastEffect.nextEffect = Vc.firstEffect), e.lastEffect = Vc.lastEffect), 1 < Vc.effectTag && (null !== e.lastEffect ? e.lastEffect.nextEffect = Vc : e.firstEffect = Vc, e.lastEffect = Vc))
				} else {
					if (null !== (t = ac(Vc))) return t.effectTag &= 2047, t;
					null !== e && (e.firstEffect = e.lastEffect = null, e.effectTag |= 2048)
				}
				if (null !== (t = Vc.sibling)) return t;
				Vc = e
			} while (null !== Vc);
			return Wc === Mc && (Wc = Uc), null
		}

		function Cs(e) {
			var t = e.expirationTime;
			return t > (e = e.childExpirationTime) ? t : e
		}

		function As(e) {
			var t = Qo();
			return Yo(99, Os.bind(null, e, t)), null
		}

		function Os(e, t) {
			do {
				Rs()
			} while (null !== is);
			if ((Nc & (Bc | Rc)) !== Ac) throw Error(n(327));
			var r = e.finishedWork,
				o = e.finishedExpirationTime;
			if (null === r) return null;
			if (e.finishedWork = null, e.finishedExpirationTime = 0, r === e.current) throw Error(n(177));
			e.callbackNode = null, e.callbackExpirationTime = 0, e.callbackPriority = 90, e.nextKnownPendingLevel = 0;
			var i = Cs(r);
			if (e.firstPendingTime = i, o <= e.lastSuspendedTime ? e.firstSuspendedTime = e.lastSuspendedTime = e.nextKnownPendingLevel = 0 : o <= e.firstSuspendedTime && (e.firstSuspendedTime = o - 1), o <= e.lastPingedTime && (e.lastPingedTime = 0), o <= e.lastExpiredTime && (e.lastExpiredTime = 0), e === Hc && (Vc = Hc = null, qc = 0), 1 < r.effectTag ? null !== r.lastEffect ? (r.lastEffect.nextEffect = r, i = r.firstEffect) : i = r : i = r.firstEffect, null !== i) {
				var a = Nc;
				Nc |= Rc, Cc.current = null, kr = Qt;
				var c = hr();
				if (vr(c)) {
					if ("selectionStart" in c) var s = {
						start: c.selectionStart,
						end: c.selectionEnd
					};
					else e: {
						var u = (s = (s = c.ownerDocument) && s.defaultView || window).getSelection && s.getSelection();
						if (u && 0 !== u.rangeCount) {
							s = u.anchorNode;
							var l = u.anchorOffset,
								d = u.focusNode;
							u = u.focusOffset;
							try {
								s.nodeType, d.nodeType
							} catch (e) {
								s = null;
								break e
							}
							var p = 0,
								f = -1,
								m = -1,
								g = 0,
								h = 0,
								v = c,
								b = null;
							t: for (;;) {
								for (var y; v !== s || 0 !== l && 3 !== v.nodeType || (f = p + l), v !== d || 0 !== u && 3 !== v.nodeType || (m = p + u), 3 === v.nodeType && (p += v.nodeValue.length), null !== (y = v.firstChild);) b = v, v = y;
								for (;;) {
									if (v === c) break t;
									if (b === s && ++g === l && (f = p), b === d && ++h === u && (m = p), null !== (y = v.nextSibling)) break;
									b = (v = b).parentNode
								}
								v = y
							}
							s = -1 === f || -1 === m ? null : {
								start: f,
								end: m
							}
						} else s = null
					}
					s = s || {
						start: 0,
						end: 0
					}
				} else s = null;
				xr = {
					activeElementDetached: null,
					focusedElem: c,
					selectionRange: s
				}, Qt = !1, es = i;
				do {
					try {
						Bs()
					} catch (e) {
						if (null === es) throw Error(n(330));
						Fs(es, e), es = es.nextEffect
					}
				} while (null !== es);
				es = i;
				do {
					try {
						for (c = e, s = t; null !== es;) {
							var _ = es.effectTag;
							if (16 & _ && Ve(es.stateNode, ""), 128 & _) {
								var w = es.alternate;
								if (null !== w) {
									var k = w.ref;
									null !== k && ("function" == typeof k ? k(null) : k.current = null)
								}
							}
							switch (1038 & _) {
								case 2:
									bc(es), es.effectTag &= -3;
									break;
								case 6:
									bc(es), es.effectTag &= -3, kc(es.alternate, es);
									break;
								case 1024:
									es.effectTag &= -1025;
									break;
								case 1028:
									es.effectTag &= -1025, kc(es.alternate, es);
									break;
								case 4:
									kc(es.alternate, es);
									break;
								case 8:
									wc(c, l = es, s), hc(l)
							}
							es = es.nextEffect
						}
					} catch (e) {
						if (null === es) throw Error(n(330));
						Fs(es, e), es = es.nextEffect
					}
				} while (null !== es);
				if (k = xr, w = hr(), _ = k.focusedElem, s = k.selectionRange, w !== _ && _ && _.ownerDocument && gr(_.ownerDocument.documentElement, _)) {
					null !== s && vr(_) && (w = s.start, void 0 === (k = s.end) && (k = w), "selectionStart" in _ ? (_.selectionStart = w, _.selectionEnd = Math.min(k, _.value.length)) : (k = (w = _.ownerDocument || document) && w.defaultView || window).getSelection && (k = k.getSelection(), l = _.textContent.length, c = Math.min(s.start, l), s = void 0 === s.end ? c : Math.min(s.end, l), !k.extend && c > s && (l = s, s = c, c = l), l = mr(_, c), d = mr(_, s), l && d && (1 !== k.rangeCount || k.anchorNode !== l.node || k.anchorOffset !== l.offset || k.focusNode !== d.node || k.focusOffset !== d.offset) && ((w = w.createRange()).setStart(l.node, l.offset), k.removeAllRanges(), c > s ? (k.addRange(w), k.extend(d.node, d.offset)) : (w.setEnd(d.node, d.offset), k.addRange(w))))), w = [];
					for (k = _; k = k.parentNode;) 1 === k.nodeType && w.push({
						element: k,
						left: k.scrollLeft,
						top: k.scrollTop
					});
					for ("function" == typeof _.focus && _.focus(), _ = 0; _ < w.length; _++)(k = w[_]).element.scrollLeft = k.left, k.element.scrollTop = k.top
				}
				Qt = !!kr, xr = kr = null, e.current = r, es = i;
				do {
					try {
						for (_ = e; null !== es;) {
							var x = es.effectTag;
							if (36 & x && mc(_, es.alternate, es), 128 & x) {
								w = void 0;
								var T = es.ref;
								if (null !== T) {
									var S = es.stateNode;
									es.tag, w = S, "function" == typeof T ? T(w) : T.current = w
								}
							}
							es = es.nextEffect
						}
					} catch (e) {
						if (null === es) throw Error(n(330));
						Fs(es, e), es = es.nextEffect
					}
				} while (null !== es);
				es = null, Vo(), Nc = a
			} else e.current = r;
			if (os) os = !1, is = e, as = t;
			else
				for (es = i; null !== es;) t = es.nextEffect, es.nextEffect = null, es = t;
			if (0 === (t = e.firstPendingTime) && (ns = null), 1073741823 === t ? e === us ? ss++ : (ss = 0, us = e) : ss = 0, "function" == typeof Us && Us(r.stateNode, o), hs(e), ts) throw ts = !1, e = rs, rs = null, e;
			return (Nc & Oc) !== Ac || ei(), null
		}

		function Bs() {
			for (; null !== es;) {
				var e = es.effectTag;
				0 != (256 & e) && dc(es.alternate, es), 0 == (512 & e) || os || (os = !0, Jo(97, (function() {
					return Rs(), null
				}))), es = es.nextEffect
			}
		}

		function Rs() {
			if (90 !== as) {
				var e = 97 < as ? 97 : as;
				return as = 90, Yo(e, Ms)
			}
		}

		function Ms() {
			if (null === is) return !1;
			var e = is;
			if (is = null, (Nc & (Bc | Rc)) !== Ac) throw Error(n(331));
			var t = Nc;
			for (Nc |= Rc, e = e.current.firstEffect; null !== e;) {
				try {
					var r = e;
					if (0 != (512 & r.effectTag)) switch (r.tag) {
						case 0:
						case 11:
						case 15:
						case 22:
							pc(5, r), fc(5, r)
					}
				} catch (t) {
					if (null === e) throw Error(n(330));
					Fs(e, t)
				}
				r = e.nextEffect, e.nextEffect = null, e = r
			}
			return Nc = t, ei(), !0
		}

		function zs(e, t, r) {
			vi(e, t = Sc(e, t = cc(r, t), 1073741823)), null !== (e = ms(e, 1073741823)) && hs(e)
		}

		function Fs(e, t) {
			if (3 === e.tag) zs(e, e, t);
			else
				for (var r = e.return; null !== r;) {
					if (3 === r.tag) {
						zs(r, e, t);
						break
					}
					if (1 === r.tag) {
						var n = r.stateNode;
						if ("function" == typeof r.type.getDerivedStateFromError || "function" == typeof n.componentDidCatch && (null === ns || !ns.has(n))) {
							vi(r, e = Ic(r, e = cc(t, e), 1073741823)), null !== (r = ms(r, 1073741823)) && hs(r);
							break
						}
					}
					r = r.return
				}
		}

		function js(e, t, r) {
			var n = e.pingCache;
			null !== n && n.delete(t), Hc === e && qc === r ? Wc === Lc || Wc === jc && 1073741823 === Kc && $o() - Jc < Zc ? ws(e, qc) : Yc = !0 : Ys(e, r) && (0 !== (t = e.lastPingedTime) && t < r || (e.lastPingedTime = r, hs(e)))
		}

		function Ls(e, t) {
			var r = e.stateNode;
			null !== r && r.delete(t), 0 === (t = 0) && (t = ps(t = ds(), e, null)), null !== (e = ms(e, t)) && hs(e)
		}
		Ec = function(e, t, r) {
			var o = t.expirationTime;
			if (null !== e) {
				var i = t.pendingProps;
				if (e.memoizedProps !== i || wo.current) ja = !0;
				else {
					if (o < r) {
						switch (ja = !1, t.tag) {
							case 3:
								Ka(t), za();
								break;
							case 5:
								if (Hi(t), 4 & t.mode && 1 !== r && i.hidden) return t.expirationTime = t.childExpirationTime = 1, null;
								break;
							case 1:
								To(t.type) && Po(t);
								break;
							case 4:
								Ui(t, t.stateNode.containerInfo);
								break;
							case 10:
								o = t.memoizedProps.value, i = t.type._context, bo(oi, i._currentValue), i._currentValue = o;
								break;
							case 13:
								if (null !== t.memoizedState) return 0 !== (o = t.child.childExpirationTime) && o >= r ? Za(e, t, r) : (bo(qi, 1 & qi.current), null !== (t = nc(e, t, r)) ? t.sibling : null);
								bo(qi, 1 & qi.current);
								break;
							case 19:
								if (o = t.childExpirationTime >= r, 0 != (64 & e.effectTag)) {
									if (o) return rc(e, t, r);
									t.effectTag |= 64
								}
								if (null !== (i = t.memoizedState) && (i.rendering = null, i.tail = null), bo(qi, qi.current), !o) return null
						}
						return nc(e, t, r)
					}
					ja = !1
				}
			} else ja = !1;
			switch (t.expirationTime = 0, t.tag) {
				case 2:
					if (o = t.type, null !== e && (e.alternate = null, t.alternate = null, t.effectTag |= 2), e = t.pendingProps, i = xo(t, _o.current), di(t, r), i = ra(null, t, o, e, i, r), t.effectTag |= 1, "object" == typeof i && null !== i && "function" == typeof i.render && void 0 === i.$$typeof) {
						if (t.tag = 1, t.memoizedState = null, t.updateQueue = null, To(o)) {
							var a = !0;
							Po(t)
						} else a = !1;
						t.memoizedState = null !== i.state && void 0 !== i.state ? i.state : null, mi(t);
						var c = o.getDerivedStateFromProps;
						"function" == typeof c && xi(t, o, c, e), i.updater = Ti, t.stateNode = i, i._reactInternalFiber = t, Pi(t, o, e, r), t = Ga(null, t, o, !0, a, r)
					} else t.tag = 0, La(null, t, i, r), t = t.child;
					return t;
				case 16:
					e: {
						if (i = t.elementType, null !== e && (e.alternate = null, t.alternate = null, t.effectTag |= 2), e = t.pendingProps, function(e) {
								if (-1 === e._status) {
									e._status = 0;
									var t = e._ctor;
									t = t(), e._result = t, t.then((function(t) {
										0 === e._status && (t = t.default, e._status = 1, e._result = t)
									}), (function(t) {
										0 === e._status && (e._status = 2, e._result = t)
									}))
								}
							}(i), 1 !== i._status) throw i._result;
						switch (i = i._result, t.type = i, a = t.tag = function(e) {
							if ("function" == typeof e) return qs(e) ? 1 : 0;
							if (null != e) {
								if ((e = e.$$typeof) === le) return 11;
								if (e === fe) return 14
							}
							return 2
						}(i), e = ni(i, e), a) {
							case 0:
								t = qa(null, t, i, e, r);
								break e;
							case 1:
								t = Wa(null, t, i, e, r);
								break e;
							case 11:
								t = Ua(null, t, i, e, r);
								break e;
							case 14:
								t = Na(null, t, i, ni(i.type, e), o, r);
								break e
						}
						throw Error(n(306, i, ""))
					}
					return t;
				case 0:
					return o = t.type, i = t.pendingProps, qa(e, t, o, i = t.elementType === o ? i : ni(o, i), r);
				case 1:
					return o = t.type, i = t.pendingProps, Wa(e, t, o, i = t.elementType === o ? i : ni(o, i), r);
				case 3:
					if (Ka(t), o = t.updateQueue, null === e || null === o) throw Error(n(282));
					if (o = t.pendingProps, i = null !== (i = t.memoizedState) ? i.element : null, gi(e, t), yi(t, o, null, r), (o = t.memoizedState.element) === i) za(), t = nc(e, t, r);
					else {
						if ((i = t.stateNode.hydrate) && (Da = Pr(t.stateNode.containerInfo.firstChild), Pa = t, i = Ca = !0), i)
							for (r = Ri(t, null, o, r), t.child = r; r;) r.effectTag = -3 & r.effectTag | 1024, r = r.sibling;
						else La(e, t, o, r), za();
						t = t.child
					}
					return t;
				case 5:
					return Hi(t), null === e && Ba(t), o = t.type, i = t.pendingProps, a = null !== e ? e.memoizedProps : null, c = i.children, Sr(o, i) ? c = null : null !== a && Sr(o, a) && (t.effectTag |= 16), Va(e, t), 4 & t.mode && 1 !== r && i.hidden ? (t.expirationTime = t.childExpirationTime = 1, t = null) : (La(e, t, c, r), t = t.child), t;
				case 6:
					return null === e && Ba(t), null;
				case 13:
					return Za(e, t, r);
				case 4:
					return Ui(t, t.stateNode.containerInfo), o = t.pendingProps, null === e ? t.child = Bi(t, null, o, r) : La(e, t, o, r), t.child;
				case 11:
					return o = t.type, i = t.pendingProps, Ua(e, t, o, i = t.elementType === o ? i : ni(o, i), r);
				case 7:
					return La(e, t, t.pendingProps, r), t.child;
				case 8:
				case 12:
					return La(e, t, t.pendingProps.children, r), t.child;
				case 10:
					e: {
						o = t.type._context,
						i = t.pendingProps,
						c = t.memoizedProps,
						a = i.value;
						var s = t.type._context;
						if (bo(oi, s._currentValue), s._currentValue = a, null !== c)
							if (s = c.value, 0 === (a = Wn(s, a) ? 0 : 0 | ("function" == typeof o._calculateChangedBits ? o._calculateChangedBits(s, a) : 1073741823))) {
								if (c.children === i.children && !wo.current) {
									t = nc(e, t, r);
									break e
								}
							} else
								for (null !== (s = t.child) && (s.return = t); null !== s;) {
									var u = s.dependencies;
									if (null !== u) {
										c = s.child;
										for (var l = u.firstContext; null !== l;) {
											if (l.context === o && 0 != (l.observedBits & a)) {
												1 === s.tag && ((l = hi(r, null)).tag = 2, vi(s, l)), s.expirationTime < r && (s.expirationTime = r), null !== (l = s.alternate) && l.expirationTime < r && (l.expirationTime = r), li(s.return, r), u.expirationTime < r && (u.expirationTime = r);
												break
											}
											l = l.next
										}
									} else c = 10 === s.tag && s.type === t.type ? null : s.child;
									if (null !== c) c.return = s;
									else
										for (c = s; null !== c;) {
											if (c === t) {
												c = null;
												break
											}
											if (null !== (s = c.sibling)) {
												s.return = c.return, c = s;
												break
											}
											c = c.return
										}
									s = c
								}
						La(e, t, i.children, r),
						t = t.child
					}
					return t;
				case 9:
					return i = t.type, o = (a = t.pendingProps).children, di(t, r), o = o(i = pi(i, a.unstable_observedBits)), t.effectTag |= 1, La(e, t, o, r), t.child;
				case 14:
					return a = ni(i = t.type, t.pendingProps), Na(e, t, i, a = ni(i.type, a), o, r);
				case 15:
					return Ha(e, t, t.type, t.pendingProps, o, r);
				case 17:
					return o = t.type, i = t.pendingProps, i = t.elementType === o ? i : ni(o, i), null !== e && (e.alternate = null, t.alternate = null, t.effectTag |= 2), t.tag = 1, To(o) ? (e = !0, Po(t)) : e = !1, di(t, r), Ii(t, o, i), Pi(t, o, i, r), Ga(null, t, o, !0, e, r);
				case 19:
					return rc(e, t, r)
			}
			throw Error(n(156, t.tag))
		};
		var Us = null,
			Ns = null;

		function Hs(e, t, r, n) {
			this.tag = e, this.key = r, this.sibling = this.child = this.return = this.stateNode = this.type = this.elementType = null, this.index = 0, this.ref = null, this.pendingProps = t, this.dependencies = this.memoizedState = this.updateQueue = this.memoizedProps = null, this.mode = n, this.effectTag = 0, this.lastEffect = this.firstEffect = this.nextEffect = null, this.childExpirationTime = this.expirationTime = 0, this.alternate = null
		}

		function Vs(e, t, r, n) {
			return new Hs(e, t, r, n)
		}

		function qs(e) {
			return !(!(e = e.prototype) || !e.isReactComponent)
		}

		function Ws(e, t) {
			var r = e.alternate;
			return null === r ? ((r = Vs(e.tag, t, e.key, e.mode)).elementType = e.elementType, r.type = e.type, r.stateNode = e.stateNode, r.alternate = e, e.alternate = r) : (r.pendingProps = t, r.effectTag = 0, r.nextEffect = null, r.firstEffect = null, r.lastEffect = null), r.childExpirationTime = e.childExpirationTime, r.expirationTime = e.expirationTime, r.child = e.child, r.memoizedProps = e.memoizedProps, r.memoizedState = e.memoizedState, r.updateQueue = e.updateQueue, t = e.dependencies, r.dependencies = null === t ? null : {
				expirationTime: t.expirationTime,
				firstContext: t.firstContext,
				responders: t.responders
			}, r.sibling = e.sibling, r.index = e.index, r.ref = e.ref, r
		}

		function Gs(e, t, r, o, i, a) {
			var c = 2;
			if (o = e, "function" == typeof e) qs(e) && (c = 1);
			else if ("string" == typeof e) c = 5;
			else e: switch (e) {
				case oe:
					return Ks(r.children, i, a, t);
				case ue:
					c = 8, i |= 7;
					break;
				case ie:
					c = 8, i |= 1;
					break;
				case ae:
					return (e = Vs(12, r, t, 8 | i)).elementType = ae, e.type = ae, e.expirationTime = a, e;
				case de:
					return (e = Vs(13, r, t, i)).type = de, e.elementType = de, e.expirationTime = a, e;
				case pe:
					return (e = Vs(19, r, t, i)).elementType = pe, e.expirationTime = a, e;
				default:
					if ("object" == typeof e && null !== e) switch (e.$$typeof) {
						case ce:
							c = 10;
							break e;
						case se:
							c = 9;
							break e;
						case le:
							c = 11;
							break e;
						case fe:
							c = 14;
							break e;
						case me:
							c = 16, o = null;
							break e;
						case ge:
							c = 22;
							break e
					}
					throw Error(n(130, null == e ? e : typeof e, ""))
			}
			return (t = Vs(c, r, t, i)).elementType = e, t.type = o, t.expirationTime = a, t
		}

		function Ks(e, t, r, n) {
			return (e = Vs(7, e, n, t)).expirationTime = r, e
		}

		function $s(e, t, r) {
			return (e = Vs(6, e, null, t)).expirationTime = r, e
		}

		function Qs(e, t, r) {
			return (t = Vs(4, null !== e.children ? e.children : [], e.key, t)).expirationTime = r, t.stateNode = {
				containerInfo: e.containerInfo,
				pendingChildren: null,
				implementation: e.implementation
			}, t
		}

		function Xs(e, t, r) {
			this.tag = t, this.current = null, this.containerInfo = e, this.pingCache = this.pendingChildren = null, this.finishedExpirationTime = 0, this.finishedWork = null, this.timeoutHandle = -1, this.pendingContext = this.context = null, this.hydrate = r, this.callbackNode = null, this.callbackPriority = 90, this.lastExpiredTime = this.lastPingedTime = this.nextKnownPendingLevel = this.lastSuspendedTime = this.firstSuspendedTime = this.firstPendingTime = 0
		}

		function Ys(e, t) {
			var r = e.firstSuspendedTime;
			return e = e.lastSuspendedTime, 0 !== r && r >= t && e <= t
		}

		function Js(e, t) {
			var r = e.firstSuspendedTime,
				n = e.lastSuspendedTime;
			r < t && (e.firstSuspendedTime = t), (n > t || 0 === r) && (e.lastSuspendedTime = t), t <= e.lastPingedTime && (e.lastPingedTime = 0), t <= e.lastExpiredTime && (e.lastExpiredTime = 0)
		}

		function Zs(e, t) {
			t > e.firstPendingTime && (e.firstPendingTime = t);
			var r = e.firstSuspendedTime;
			0 !== r && (t >= r ? e.firstSuspendedTime = e.lastSuspendedTime = e.nextKnownPendingLevel = 0 : t >= e.lastSuspendedTime && (e.lastSuspendedTime = t + 1), t > e.nextKnownPendingLevel && (e.nextKnownPendingLevel = t))
		}

		function eu(e, t) {
			var r = e.lastExpiredTime;
			(0 === r || r > t) && (e.lastExpiredTime = t)
		}

		function tu(e, t, r, o) {
			var i = t.current,
				a = ds(),
				c = wi.suspense;
			a = ps(a, i, c);
			e: if (r) {
				t: {
					if (rt(r = r._reactInternalFiber) !== r || 1 !== r.tag) throw Error(n(170));
					var s = r;do {
						switch (s.tag) {
							case 3:
								s = s.stateNode.context;
								break t;
							case 1:
								if (To(s.type)) {
									s = s.stateNode.__reactInternalMemoizedMergedChildContext;
									break t
								}
						}
						s = s.return
					} while (null !== s);
					throw Error(n(171))
				}
				if (1 === r.tag) {
					var u = r.type;
					if (To(u)) {
						r = Eo(r, u, s);
						break e
					}
				}
				r = s
			}
			else r = yo;
			return null === t.context ? t.context = r : t.pendingContext = r, (t = hi(a, c)).payload = {
				element: e
			}, null !== (o = void 0 === o ? null : o) && (t.callback = o), vi(i, t), fs(i, a), a
		}

		function ru(e) {
			return (e = e.current).child ? (e.child.tag, e.child.stateNode) : null
		}

		function nu(e, t) {
			null !== (e = e.memoizedState) && null !== e.dehydrated && e.retryTime < t && (e.retryTime = t)
		}

		function ou(e, t) {
			nu(e, t), (e = e.alternate) && nu(e, t)
		}

		function iu(e, t, r) {
			var n = new Xs(e, t, r = null != r && !0 === r.hydrate),
				o = Vs(3, null, null, 2 === t ? 7 : 1 === t ? 3 : 0);
			n.current = o, o.stateNode = n, mi(o), e[Br] = n.current, r && 0 !== t && function(e, t) {
				var r = tt(t);
				Dt.forEach((function(e) {
					vt(e, t, r)
				})), Ct.forEach((function(e) {
					vt(e, t, r)
				}))
			}(0, 9 === e.nodeType ? e : e.ownerDocument), this._internalRoot = n
		}

		function au(e) {
			return !(!e || 1 !== e.nodeType && 9 !== e.nodeType && 11 !== e.nodeType && (8 !== e.nodeType || " react-mount-point-unstable " !== e.nodeValue))
		}

		function cu(e, t, r, n, o) {
			var i = r._reactRootContainer;
			if (i) {
				var a = i._internalRoot;
				if ("function" == typeof o) {
					var c = o;
					o = function() {
						var e = ru(a);
						c.call(e)
					}
				}
				tu(t, a, e, o)
			} else {
				if (i = r._reactRootContainer = function(e, t) {
						if (t || (t = !(!(t = e ? 9 === e.nodeType ? e.documentElement : e.firstChild : null) || 1 !== t.nodeType || !t.hasAttribute("data-reactroot"))), !t)
							for (var r; r = e.lastChild;) e.removeChild(r);
						return new iu(e, 0, t ? {
							hydrate: !0
						} : void 0)
					}(r, n), a = i._internalRoot, "function" == typeof o) {
					var s = o;
					o = function() {
						var e = ru(a);
						s.call(e)
					}
				}
				_s((function() {
					tu(t, a, e, o)
				}))
			}
			return ru(a)
		}

		function su(e, t) {
			var r = 2 < arguments.length && void 0 !== arguments[2] ? arguments[2] : null;
			if (!au(t)) throw Error(n(200));
			return function(e, t, r) {
				var n = 3 < arguments.length && void 0 !== arguments[3] ? arguments[3] : null;
				return {
					$$typeof: ne,
					key: null == n ? null : "" + n,
					children: e,
					containerInfo: t,
					implementation: r
				}
			}(e, t, null, r)
		}
		iu.prototype.render = function(e) {
			tu(e, this._internalRoot, null, null)
		}, iu.prototype.unmount = function() {
			var e = this._internalRoot,
				t = e.containerInfo;
			tu(null, e, null, (function() {
				t[Br] = null
			}))
		}, bt = function(e) {
			if (13 === e.tag) {
				var t = ri(ds(), 150, 100);
				fs(e, t), ou(e, t)
			}
		}, yt = function(e) {
			13 === e.tag && (fs(e, 3), ou(e, 3))
		}, _t = function(e) {
			if (13 === e.tag) {
				var t = ds();
				fs(e, t = ps(t, e, null)), ou(e, t)
			}
		}, C = function(e, t, r) {
			switch (t) {
				case "input":
					if (Ee(e, r), t = r.name, "radio" === r.type && null != t) {
						for (r = e; r.parentNode;) r = r.parentNode;
						for (r = r.querySelectorAll("input[name=" + JSON.stringify("" + t) + '][type="radio"]'), t = 0; t < r.length; t++) {
							var o = r[t];
							if (o !== e && o.form === e.form) {
								var i = Fr(o);
								if (!i) throw Error(n(90));
								xe(o), Ee(o, i)
							}
						}
					}
					break;
				case "textarea":
					Re(e, r);
					break;
				case "select":
					null != (t = r.value) && Ae(e, !!r.multiple, t, !1)
			}
		}, z = ys, F = function(e, t, r, n, o) {
			var i = Nc;
			Nc |= 4;
			try {
				return Yo(98, e.bind(null, t, r, n, o))
			} finally {
				(Nc = i) === Ac && ei()
			}
		}, j = function() {
			(Nc & (1 | Bc | Rc)) === Ac && (function() {
				if (null !== cs) {
					var e = cs;
					cs = null, e.forEach((function(e, t) {
						eu(t, e), hs(t)
					})), ei()
				}
			}(), Rs())
		}, L = function(e, t) {
			var r = Nc;
			Nc |= 2;
			try {
				return e(t)
			} finally {
				(Nc = r) === Ac && ei()
			}
		};
		var uu = {
			Events: [Mr, zr, Fr, P, S, qr, function(e) {
				ct(e, Vr)
			}, R, M, er, lt, Rs, {
				current: !1
			}]
		};
		return function(e) {
			var r = e.findFiberByHostInstance;
			(function(e) {
				if ("undefined" == typeof __REACT_DEVTOOLS_GLOBAL_HOOK__) return !1;
				var t = __REACT_DEVTOOLS_GLOBAL_HOOK__;
				if (t.isDisabled || !t.supportsFiber) return !0;
				try {
					var r = t.inject(e);
					Us = function(e) {
						try {
							t.onCommitFiberRoot(r, e, void 0, 64 == (64 & e.current.effectTag))
						} catch (e) {}
					}, Ns = function(e) {
						try {
							t.onCommitFiberUnmount(r, e)
						} catch (e) {}
					}
				} catch (e) {}
			})(t({}, e, {
				overrideHookState: null,
				overrideProps: null,
				setSuspenseHandler: null,
				scheduleUpdate: null,
				currentDispatcherRef: J.ReactCurrentDispatcher,
				findHostInstanceByFiber: function(e) {
					return null === (e = it(e)) ? null : e.stateNode
				},
				findFiberByHostInstance: function(e) {
					return r ? r(e) : null
				},
				findHostInstancesForRefresh: null,
				scheduleRefresh: null,
				scheduleRoot: null,
				setRefreshHandler: null,
				getCurrentFiber: null
			}))
		}({
			findFiberByHostInstance: Rr,
			bundleType: 0,
			version: "16.14.0",
			rendererPackageName: "react-dom"
		}), b.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED = uu, b.createPortal = su, b.findDOMNode = function(e) {
			if (null == e) return null;
			if (1 === e.nodeType) return e;
			var t = e._reactInternalFiber;
			if (void 0 === t) {
				if ("function" == typeof e.render) throw Error(n(188));
				throw Error(n(268, Object.keys(e)))
			}
			return e = null === (e = it(t)) ? null : e.stateNode
		}, b.flushSync = function(e, t) {
			if ((Nc & (Bc | Rc)) !== Ac) throw Error(n(187));
			var r = Nc;
			Nc |= 1;
			try {
				return Yo(99, e.bind(null, t))
			} finally {
				Nc = r, ei()
			}
		}, b.hydrate = function(e, t, r) {
			if (!au(t)) throw Error(n(200));
			return cu(null, e, t, !0, r)
		}, b.render = function(e, t, r) {
			if (!au(t)) throw Error(n(200));
			return cu(null, e, t, !1, r)
		}, b.unmountComponentAtNode = function(e) {
			if (!au(e)) throw Error(n(40));
			return !!e._reactRootContainer && (_s((function() {
				cu(null, null, e, !1, (function() {
					e._reactRootContainer = null, e[Br] = null
				}))
			})), !0)
		}, b.unstable_batchedUpdates = ys, b.unstable_createPortal = function(e, t) {
			return su(e, t, 2 < arguments.length && void 0 !== arguments[2] ? arguments[2] : null)
		}, b.unstable_renderSubtreeIntoContainer = function(e, t, r, o) {
			if (!au(r)) throw Error(n(200));
			if (null == e || void 0 === e._reactInternalFiber) throw Error(n(38));
			return cu(e, t, r, !1, o)
		}, b.version = "16.14.0", b
	}! function(e) {
		! function e() {
			if ("undefined" != typeof __REACT_DEVTOOLS_GLOBAL_HOOK__ && "function" == typeof __REACT_DEVTOOLS_GLOBAL_HOOK__.checkDCE) try {
				__REACT_DEVTOOLS_GLOBAL_HOOK__.checkDCE(e)
			} catch (e) {
				console.error(e)
			}
		}(), e.exports = x()
	}({
		get exports() {
			return v
		},
		set exports(e) {
			v = e
		}
	});
	var T = r(v),
		S = {},
		I = {},
		E = {},
		P = {};
	! function(e) {
		var t = Object.prototype.hasOwnProperty,
			r = "~";

		function n() {}

		function o(e, t, r) {
			this.fn = e, this.context = t, this.once = r || !1
		}

		function i(e, t, n, i, a) {
			if ("function" != typeof n) throw new TypeError("The listener must be a function");
			var c = new o(n, i || e, a),
				s = r ? r + t : t;
			return e._events[s] ? e._events[s].fn ? e._events[s] = [e._events[s], c] : e._events[s].push(c) : (e._events[s] = c, e._eventsCount++), e
		}

		function a(e, t) {
			0 == --e._eventsCount ? e._events = new n : delete e._events[t]
		}

		function c() {
			this._events = new n, this._eventsCount = 0
		}
		Object.create && (n.prototype = Object.create(null), (new n).__proto__ || (r = !1)), c.prototype.eventNames = function() {
			var e, n, o = [];
			if (0 === this._eventsCount) return o;
			for (n in e = this._events) t.call(e, n) && o.push(r ? n.slice(1) : n);
			return Object.getOwnPropertySymbols ? o.concat(Object.getOwnPropertySymbols(e)) : o
		}, c.prototype.listeners = function(e) {
			var t = r ? r + e : e,
				n = this._events[t];
			if (!n) return [];
			if (n.fn) return [n.fn];
			for (var o = 0, i = n.length, a = new Array(i); o < i; o++) a[o] = n[o].fn;
			return a
		}, c.prototype.listenerCount = function(e) {
			var t = r ? r + e : e,
				n = this._events[t];
			return n ? n.fn ? 1 : n.length : 0
		}, c.prototype.emit = function(e, t, n, o, i, a) {
			var c = r ? r + e : e;
			if (!this._events[c]) return !1;
			var s, u, l = this._events[c],
				d = arguments.length;
			if (l.fn) {
				switch (l.once && this.removeListener(e, l.fn, void 0, !0), d) {
					case 1:
						return l.fn.call(l.context), !0;
					case 2:
						return l.fn.call(l.context, t), !0;
					case 3:
						return l.fn.call(l.context, t, n), !0;
					case 4:
						return l.fn.call(l.context, t, n, o), !0;
					case 5:
						return l.fn.call(l.context, t, n, o, i), !0;
					case 6:
						return l.fn.call(l.context, t, n, o, i, a), !0
				}
				for (u = 1, s = new Array(d - 1); u < d; u++) s[u - 1] = arguments[u];
				l.fn.apply(l.context, s)
			} else {
				var p, f = l.length;
				for (u = 0; u < f; u++) switch (l[u].once && this.removeListener(e, l[u].fn, void 0, !0), d) {
					case 1:
						l[u].fn.call(l[u].context);
						break;
					case 2:
						l[u].fn.call(l[u].context, t);
						break;
					case 3:
						l[u].fn.call(l[u].context, t, n);
						break;
					case 4:
						l[u].fn.call(l[u].context, t, n, o);
						break;
					default:
						if (!s)
							for (p = 1, s = new Array(d - 1); p < d; p++) s[p - 1] = arguments[p];
						l[u].fn.apply(l[u].context, s)
				}
			}
			return !0
		}, c.prototype.on = function(e, t, r) {
			return i(this, e, t, r, !1)
		}, c.prototype.once = function(e, t, r) {
			return i(this, e, t, r, !0)
		}, c.prototype.removeListener = function(e, t, n, o) {
			var i = r ? r + e : e;
			if (!this._events[i]) return this;
			if (!t) return a(this, i), this;
			var c = this._events[i];
			if (c.fn) c.fn !== t || o && !c.once || n && c.context !== n || a(this, i);
			else {
				for (var s = 0, u = [], l = c.length; s < l; s++)(c[s].fn !== t || o && !c[s].once || n && c[s].context !== n) && u.push(c[s]);
				u.length ? this._events[i] = 1 === u.length ? u[0] : u : a(this, i)
			}
			return this
		}, c.prototype.removeAllListeners = function(e) {
			var t;
			return e ? (t = r ? r + e : e, this._events[t] && a(this, t)) : (this._events = new n, this._eventsCount = 0), this
		}, c.prototype.off = c.prototype.removeListener, c.prototype.addListener = c.prototype.on, c.prefixed = r, c.EventEmitter = c, e.exports = c
	}({
		get exports() {
			return P
		},
		set exports(e) {
			P = e
		}
	});
	var D, C = {},
		A = t && t.__extends || (D = Object.setPrototypeOf || {
				__proto__: []
			}
			instanceof Array && function(e, t) {
				e.__proto__ = t
			} || function(e, t) {
				for (var r in t) t.hasOwnProperty(r) && (e[r] = t[r])
			},
			function(e, t) {
				function r() {
					this.constructor = e
				}
				D(e, t), e.prototype = null === t ? Object.create(t) : (r.prototype = t.prototype, new r)
			});
	Object.defineProperty(C, "__esModule", {
		value: !0
	});
	var O = function(e) {
		function t(r, n, o) {
			var i = e.call(this, "Error #" + r + ": " + n) || this;
			return i.code = r, i.message = n, i.path = o, Object.setPrototypeOf(i, t.prototype), i
		}
		return A(t, e), t.prototype.toReplyError = function() {
			return {
				code: this.code,
				message: this.message,
				path: this.path
			}
		}, t
	}(Error);
	C.RPCError = O;
	var B = {};
	Object.defineProperty(B, "__esModule", {
		value: !0
	});
	var R = function() {
		function e() {
			this.lastSequentialCall = -1, this.queue = []
		}
		return e.prototype.reset = function(e) {
			this.lastSequentialCall = e - 1, this.queue = []
		}, e.prototype.append = function(e) {
			if (e.counter <= this.lastSequentialCall + 1) {
				var t = [e];
				return this.lastSequentialCall = e.counter, this.replayQueue(t), t
			}
			for (var r = 0; r < this.queue.length; r++)
				if (this.queue[r].counter > e.counter) return this.queue.splice(r, 0, e), [];
			return this.queue.push(e), []
		}, e.prototype.replayQueue = function(e) {
			for (; this.queue.length;) {
				var t = this.queue[0];
				if (t.counter > this.lastSequentialCall + 1) return;
				e.push(this.queue.shift()), this.lastSequentialCall = t.counter
			}
		}, e
	}();
	B.Reorder = R;
	var M = {};
	Object.defineProperty(M, "__esModule", {
		value: !0
	}), M.isRPCMessage = function(e) {
		return ("method" === e.type || "reply" === e.type) && "number" == typeof e.counter
	}, M.defaultRecievable = {
		readMessages: function(e) {
			return window.addEventListener("message", e),
				function() {
					return window.removeEventListener("message", e)
				}
		}
	};
	var z = t && t.__extends || function() {
		var e = Object.setPrototypeOf || {
			__proto__: []
		}
		instanceof Array && function(e, t) {
			e.__proto__ = t
		} || function(e, t) {
			for (var r in t) t.hasOwnProperty(r) && (e[r] = t[r])
		};
		return function(t, r) {
			function n() {
				this.constructor = t
			}
			e(t, r), t.prototype = null === r ? Object.create(r) : (n.prototype = r.prototype, new n)
		}
	}();
	Object.defineProperty(E, "__esModule", {
		value: !0
	});
	var F = C,
		j = B,
		L = M;
	var U = function(e) {
		function t(t) {
			var r = e.call(this) || this;
			return r.options = t, r.calls = Object.create(null), r.callCounter = 0, r.reorder = new j.Reorder, r.listener = function(e) {
				if (!r.options.origin || "*" === r.options.origin || e.origin === r.options.origin) {
					var t;
					try {
						t = JSON.parse(e.data)
					} catch (e) {
						return
					}
					if (L.isRPCMessage(t) && t.serviceID === r.options.serviceId) {
						if (r.isReadySignal(t)) {
							var n = "method" === t.type ? t.params : t.result;
							n && n.protocolVersion ? r.remoteProtocolVersion = n.protocolVersion : r.remoteProtocolVersion = r.remoteProtocolVersion, r.callCounter = 0, r.reorder.reset(t.counter), r.emit("isReady", !0)
						}
						for (var o = 0, i = r.reorder.append(t); o < i.length; o++) {
							var a = i[o];
							r.emit("recvData", a), r.dispatchIncoming(a)
						}
					}
				}
			}, r.unsubscribeCallback = (t.receiver || L.defaultRecievable).readMessages(r.listener), r.isReady = new Promise((function(e) {
				var n = {
					protocolVersion: t.protocolVersion || "1.0"
				};
				r.expose("ready", (function() {
					return e(), n
				})), r.call("ready", n).then(e).catch(e)
			})), r
		}
		return z(t, e), t.prototype.create = function(e) {
			var r = new t(e);
			return r.isReady.then((function() {
				return r
			}))
		}, t.prototype.expose = function(e, t) {
			var r = this;
			return this.on(e, (function(e) {
				e.discard ? t(e.params) : new Promise((function(r) {
					return r(t(e.params))
				})).then((function(t) {
					return {
						type: "reply",
						serviceID: r.options.serviceId,
						id: e.id,
						result: t
					}
				})).catch((function(t) {
					return {
						type: "reply",
						serviceID: r.options.serviceId,
						id: e.id,
						error: t instanceof F.RPCError ? t.toReplyError() : {
							code: 0,
							message: t.stack || t.message
						}
					}
				})).then((function(e) {
					r.emit("sendReply", e), r.post(e)
				}))
			})), this
		}, t.prototype.call = function(e, t, r) {
			var n = this;
			void 0 === r && (r = !0);
			var o = "ready" === e ? -1 : this.callCounter,
				i = {
					type: "method",
					serviceID: this.options.serviceId,
					id: o,
					params: t,
					method: e,
					discard: !r
				};
			if (this.emit("sendMethod", i), this.post(i), r) return new Promise((function(e, t) {
				n.calls[o] = function(r, n) {
					r ? t(r) : e(n)
				}
			}))
		}, t.prototype.destroy = function() {
			this.emit("destroy"), this.unsubscribeCallback()
		}, t.prototype.remoteVersion = function() {
			return this.remoteProtocolVersion
		}, t.prototype.handleReply = function(e) {
			var t, r = this.calls[e.id];
			r && (e.error ? r((t = e.error, new F.RPCError(t.code, t.message, t.path)), null) : r(null, e.result), delete this.calls[e.id])
		}, t.prototype.post = function(e) {
			e.counter = this.callCounter++, this.options.target.postMessage(JSON.stringify(e), this.options.origin || "*")
		}, t.prototype.isReadySignal = function(e) {
			return "method" === e.type && "ready" === e.method || "reply" === e.type && -1 === e.id
		}, t.prototype.dispatchIncoming = function(e) {
			switch (e.type) {
				case "method":
					if (this.emit("recvMethod", e), this.listeners(e.method).length > 0) return void this.emit(e.method, e);
					this.post({
						type: "reply",
						serviceID: this.options.serviceId,
						id: e.id,
						error: {
							code: 4003,
							message: 'Unknown method name "' + e.method + '"'
						},
						result: null
					});
					break;
				case "reply":
					this.emit("recvReply", e), this.handleReply(e)
			}
		}, t
	}(P.EventEmitter);
	E.RPC = U;
	var N = t && t.__importDefault || function(e) {
		return e && e.__esModule ? e : {
			default: e
		}
	};
	Object.defineProperty(I, "__esModule", {
		value: !0
	}), I.configShim = I.withPropsReceiver = void 0;
	const H = E,
		V = N(u),
		q = "@assets/microfrontend",
		W = "reactSetProps",
		G = "reactCallback",
		K = "reactInvokeOpRef",
		$ = () => V.default.createElement("progress", null);
	I.withPropsReceiver = function(e, t = q, r = $, n = !1) {
		return class extends V.default.Component {
			constructor(e) {
				super(e), this.handleSetProps = e => {
					this.setState((t => {
						const r = {};
						return e.callbacks.forEach((e => {
							r[e] = (...t) => this.handleGeneralCallback({
								callbackName: e,
								args: t
							})
						})), {
							pushes: t.pushes + 1,
							propsMessage: e,
							generatedCallbacks: r
						}
					}))
				}, this.handleGeneralCallback = async e => await this.rpc.call(G, e), this.invokeOp = async e => {
					var t, r;
					return null === (r = null === (t = this.wrappedRef.current) || void 0 === t ? void 0 : t.invokeOp) || void 0 === r ? void 0 : r.call(t, e)
				}, this.rpc = new H.RPC({
					target: window.parent,
					serviceId: t
				}), this.rpc.expose(W, this.handleSetProps), this.rpc.expose(K, this.invokeOp), this.wrappedRef = V.default.createRef(), this.state = {
					ready: !1,
					pushes: 0,
					propsMessage: {
						simple: {},
						callbacks: []
					},
					generatedCallbacks: {}
				}
			}
			async componentDidMount() {
				await this.rpc.isReady, this.setState({
					ready: !0
				})
			}
			componentWillUnmount() {
				this.rpc.destroy()
			}
			render() {
				if (n && (!this.state.ready || this.state.pushes < 1)) return r();
				const {
					propsMessage: t,
					generatedCallbacks: o
				} = this.state;
				return V.default.createElement(e, {
					ref: this.wrappedRef,
					...this.props,
					...t.simple,
					...o
				})
			}
		}
	}, I.configShim = function(e, t = undefined, r = q, n = $) {
		var o;
		return (o = class o extends V.default.Component {
			constructor(e) {
				super(e), this.handleCallback = async e => {
					const t = this.props[e.callbackName];
					return null == t ? void 0 : t(...e.args)
				}, this.invokeOp = async e => {
					var t;
					return null === (t = this.rpc) || void 0 === t ? void 0 : t.call(K, e)
				}, this.state = {
					ready: !1
				}, this.iframeRef = V.default.createRef(), this.rpc = null
			}
			componentDidMount() {
				const e = this.iframeRef.current;
				if (!e || !e.contentWindow) throw new Error("Could not get iframe ref!");
				this.rpc = new H.RPC({
					target: e.contentWindow,
					serviceId: r
				}), this.rpc.expose(G, this.handleCallback), this.rpc.isReady.then((() => {
					this.setState({
						ready: !0
					})
				}))
			}
			componentWillUnmount() {
				var e;
				null === (e = this.rpc) || void 0 === e || e.destroy()
			}
			shouldComponentUpdate(e, t, r) {
				var n;
				if (t.ready) {
					const t = Object.entries(e).reduce(((e, [t, r]) => ("function" == typeof r ? e.callbacks.push(t) : e.simple[t] = r, e)), {
						simple: {},
						callbacks: []
					});
					null === (n = this.rpc) || void 0 === n || n.call(W, t)
				}
				return !0
			}
			render() {
				return V.default.createElement(V.default.Fragment, null, V.default.createElement("iframe", {
					"data-testid": "microfrontend",
					ref: this.iframeRef,
					src: e,
					sandbox: t,
					style: o.IFRAME_STYLE
				}), !this.state.ready && n())
			}
		}).IFRAME_STYLE = {
			width: "100%",
			height: "100%",
			border: "none"
		}, o
	};
	var Q = {},
		X = {};
	Object.defineProperty(X, "__esModule", {
		value: !0
	}), X.getUrlFor = void 0;
	const Y = {
		PROD: "experience.adobe.com",
		STAGE: "experience-stage.adobe.com",
		QA: "experience-qa.adobe.com",
		DEV: "localhost.corp.adobe.com:8443",
		DEV_443: "localhost.corp.adobe.com",
		default: "localhost.corp.adobe.com:8443"
	};
	X.getUrlFor = function(e, t, r, n) {
		const o = function(e) {
				let t = e && Y[e];
				if (!t && function(e) {
						return "config" in e
					}(window)) {
					const {
						config: e
					} = window;
					t = Y[e.env]
				}
				return t || Y.PROD
			}(r),
			i = new URL(`https://${o}`);
		return i.pathname = `solutions/${t}/static-assets/resources/embed.html`, i.searchParams.set("route", e), n ? i.searchParams.set(`${t}_version`, n) : console.warn(`No explicit version found for hosted micro-frontend solution: ${t}. Falling back to loading latest version, which could potentially be out of date due to caching.`), i.href
	};
	var J, Z, ee = {},
		te = {},
		re = {};
	var ne = (Z || (Z = 1, function(e) {
			e.exports = function() {
				if (J) return re;
				J = 1;
				var e = "function" == typeof Symbol && Symbol.for,
					t = e ? Symbol.for("react.element") : 60103,
					r = e ? Symbol.for("react.portal") : 60106,
					n = e ? Symbol.for("react.fragment") : 60107,
					o = e ? Symbol.for("react.strict_mode") : 60108,
					i = e ? Symbol.for("react.profiler") : 60114,
					a = e ? Symbol.for("react.provider") : 60109,
					c = e ? Symbol.for("react.context") : 60110,
					s = e ? Symbol.for("react.async_mode") : 60111,
					u = e ? Symbol.for("react.concurrent_mode") : 60111,
					l = e ? Symbol.for("react.forward_ref") : 60112,
					d = e ? Symbol.for("react.suspense") : 60113,
					p = e ? Symbol.for("react.suspense_list") : 60120,
					f = e ? Symbol.for("react.memo") : 60115,
					m = e ? Symbol.for("react.lazy") : 60116,
					g = e ? Symbol.for("react.block") : 60121,
					h = e ? Symbol.for("react.fundamental") : 60117,
					v = e ? Symbol.for("react.responder") : 60118,
					b = e ? Symbol.for("react.scope") : 60119;

				function y(e) {
					if ("object" == typeof e && null !== e) {
						var p = e.$$typeof;
						switch (p) {
							case t:
								switch (e = e.type) {
									case s:
									case u:
									case n:
									case i:
									case o:
									case d:
										return e;
									default:
										switch (e = e && e.$$typeof) {
											case c:
											case l:
											case m:
											case f:
											case a:
												return e;
											default:
												return p
										}
								}
								case r:
									return p
						}
					}
				}

				function _(e) {
					return y(e) === u
				}
				return re.AsyncMode = s, re.ConcurrentMode = u, re.ContextConsumer = c, re.ContextProvider = a, re.Element = t, re.ForwardRef = l, re.Fragment = n, re.Lazy = m, re.Memo = f, re.Portal = r, re.Profiler = i, re.StrictMode = o, re.Suspense = d, re.isAsyncMode = function(e) {
					return _(e) || y(e) === s
				}, re.isConcurrentMode = _, re.isContextConsumer = function(e) {
					return y(e) === c
				}, re.isContextProvider = function(e) {
					return y(e) === a
				}, re.isElement = function(e) {
					return "object" == typeof e && null !== e && e.$$typeof === t
				}, re.isForwardRef = function(e) {
					return y(e) === l
				}, re.isFragment = function(e) {
					return y(e) === n
				}, re.isLazy = function(e) {
					return y(e) === m
				}, re.isMemo = function(e) {
					return y(e) === f
				}, re.isPortal = function(e) {
					return y(e) === r
				}, re.isProfiler = function(e) {
					return y(e) === i
				}, re.isStrictMode = function(e) {
					return y(e) === o
				}, re.isSuspense = function(e) {
					return y(e) === d
				}, re.isValidElementType = function(e) {
					return "string" == typeof e || "function" == typeof e || e === n || e === u || e === i || e === o || e === d || e === p || "object" == typeof e && null !== e && (e.$$typeof === m || e.$$typeof === f || e.$$typeof === a || e.$$typeof === c || e.$$typeof === l || e.$$typeof === h || e.$$typeof === v || e.$$typeof === b || e.$$typeof === g)
				}, re.typeOf = y, re
			}()
		}({
			get exports() {
				return te
			},
			set exports(e) {
				te = e
			}
		})), te),
		oe = {
			childContextTypes: !0,
			contextType: !0,
			contextTypes: !0,
			defaultProps: !0,
			displayName: !0,
			getDefaultProps: !0,
			getDerivedStateFromError: !0,
			getDerivedStateFromProps: !0,
			mixins: !0,
			propTypes: !0,
			type: !0
		},
		ie = {
			name: !0,
			length: !0,
			prototype: !0,
			caller: !0,
			callee: !0,
			arguments: !0,
			arity: !0
		},
		ae = {
			$$typeof: !0,
			compare: !0,
			defaultProps: !0,
			displayName: !0,
			propTypes: !0,
			type: !0
		},
		ce = {};

	function se(e) {
		return ne.isMemo(e) ? ae : ce[e.$$typeof] || oe
	}
	ce[ne.ForwardRef] = {
		$$typeof: !0,
		render: !0,
		defaultProps: !0,
		displayName: !0,
		propTypes: !0
	}, ce[ne.Memo] = ae;
	var ue = Object.defineProperty,
		le = Object.getOwnPropertyNames,
		de = Object.getOwnPropertySymbols,
		pe = Object.getOwnPropertyDescriptor,
		fe = Object.getPrototypeOf,
		me = Object.prototype;
	var ge = function e(t, r, n) {
		if ("string" != typeof r) {
			if (me) {
				var o = fe(r);
				o && o !== me && e(t, o, n)
			}
			var i = le(r);
			de && (i = i.concat(de(r)));
			for (var a = se(t), c = se(r), s = 0; s < i.length; ++s) {
				var u = i[s];
				if (!(ie[u] || n && n[u] || c && c[u] || a && a[u])) {
					var l = pe(r, u);
					try {
						ue(t, u, l)
					} catch (e) {}
				}
			}
		}
		return t
	};
	! function(e) {
		var r = t && t.__createBinding || (Object.create ? function(e, t, r, n) {
				void 0 === n && (n = r);
				var o = Object.getOwnPropertyDescriptor(t, r);
				o && !("get" in o ? !t.__esModule : o.writable || o.configurable) || (o = {
					enumerable: !0,
					get: function() {
						return t[r]
					}
				}), Object.defineProperty(e, n, o)
			} : function(e, t, r, n) {
				void 0 === n && (n = r), e[n] = t[r]
			}),
			n = t && t.__setModuleDefault || (Object.create ? function(e, t) {
				Object.defineProperty(e, "default", {
					enumerable: !0,
					value: t
				})
			} : function(e, t) {
				e.default = t
			}),
			o = t && t.__importStar || function(e) {
				if (e && e.__esModule) return e;
				var t = {};
				if (null != e)
					for (var o in e) "default" !== o && Object.prototype.hasOwnProperty.call(e, o) && r(t, e, o);
				return n(t, e), t
			},
			i = t && t.__importDefault || function(e) {
				return e && e.__esModule ? e : {
					default: e
				}
			};
		Object.defineProperty(e, "__esModule", {
			value: !0
		}), e.withMicrofrontend = e.MicrofrontendProvider = e.MicrofrontendContext = void 0;
		const a = o(u),
			c = i(ge),
			s = {
				locale: "en-US",
				colorScheme: "light",
				featureFlags: [],
				onToast: void 0,
				env: "PROD",
				consumerProps: {},
				solutions: {},
				UNSAFE_passThru: {}
			};
		e.MicrofrontendContext = a.default.createContext(s);
		e.MicrofrontendProvider = ({
			locale: t,
			colorScheme: r,
			featureFlags: n,
			children: o,
			onToast: i = (() => {
				console.error('MicrofrontendProvider prop "onToast" is not implemented!')
			}),
			env: c,
			consumerProps: s,
			solutions: u,
			UNSAFE_passThru: l
		}) => {
			const d = {
				locale: t,
				colorScheme: r,
				featureFlags: n,
				onToast: i,
				env: c,
				consumerProps: s,
				solutions: u,
				...l
			};
			return a.default.createElement(e.MicrofrontendContext.Provider, {
				value: d
			}, o)
		};
		e.withMicrofrontend = t => {
			const r = r => {
				const n = (0, a.useContext)(e.MicrofrontendContext);
				return a.default.createElement(t, {
					...n,
					...r,
					ref: r.forwardedRef
				})
			};
			return (0, c.default)(a.default.forwardRef(((e, t) => a.default.createElement(r, {
				...e,
				forwardedRef: t
			}))), t)
		}
	}(ee);
	var he = t && t.__createBinding || (Object.create ? function(e, t, r, n) {
			void 0 === n && (n = r);
			var o = Object.getOwnPropertyDescriptor(t, r);
			o && !("get" in o ? !t.__esModule : o.writable || o.configurable) || (o = {
				enumerable: !0,
				get: function() {
					return t[r]
				}
			}), Object.defineProperty(e, n, o)
		} : function(e, t, r, n) {
			void 0 === n && (n = r), e[n] = t[r]
		}),
		ve = t && t.__setModuleDefault || (Object.create ? function(e, t) {
			Object.defineProperty(e, "default", {
				enumerable: !0,
				value: t
			})
		} : function(e, t) {
			e.default = t
		}),
		be = t && t.__importStar || function(e) {
			if (e && e.__esModule) return e;
			var t = {};
			if (null != e)
				for (var r in e) "default" !== r && Object.prototype.hasOwnProperty.call(e, r) && he(t, e, r);
			return ve(t, e), t
		};
	Object.defineProperty(Q, "__esModule", {
		value: !0
	});
	const ye = be(u),
		_e = X,
		we = ee,
		ke = I;
	Q.default = function(e) {
		var t;
		const r = (0, ye.useRef)(),
			{
				solutions: n
			} = ye.default.useContext(we.MicrofrontendContext),
			{
				frontend: o,
				serviceId: i,
				env: a,
				version: c,
				solutionName: s
			} = e,
			u = c || (null === (t = null == n ? void 0 : n[s]) || void 0 === t ? void 0 : t.liveVersion);
		if ((0, ye.useMemo)((() => {
				const e = (0, _e.getUrlFor)(o, s, a, u);
				r.current = (0, ke.configShim)(e, void 0, i, (() => null))
			}), [a, i, u]), !r.current) throw Error("Unable to create shim!");
		return r.current
	};
	var xe = {};
	Object.defineProperty(xe, "__esModule", {
		value: !0
	}), xe.useResolvedProps = xe.filterInterfaceProps = xe.getDefaultProps = xe.getPropTypes = void 0;
	const Te = u,
		Se = ee;

	function Ie(e, t) {
		return Object.fromEntries(Object.entries(e).filter((([e, r]) => Object.prototype.hasOwnProperty.call(t, e))))
	}
	xe.getPropTypes = function(e) {
			return Object.fromEntries(Object.entries(e).map((([e, t]) => [e, t.type])))
		}, xe.getDefaultProps = function(e) {
			return Object.fromEntries(Object.entries(e).map((([e, t]) => [e, t.defaultValue])).filter((([, e]) => void 0 !== e)))
		}, xe.filterInterfaceProps = Ie, xe.useResolvedProps = function(e, t, r) {
			var n;
			const {
				UNSAFE_passThru: o,
				...i
			} = (0, Te.useContext)(Se.MicrofrontendContext), a = r ? {
				...null === (n = i.consumerProps) || void 0 === n ? void 0 : n[r]
			} : {};
			return {
				...Ie({
					...i,
					...o
				}, t),
				...Ie({
					...a
				}, t),
				...Ie(e, t)
			}
		},
		function(e) {
			var r = t && t.__createBinding || (Object.create ? function(e, t, r, n) {
					void 0 === n && (n = r);
					var o = Object.getOwnPropertyDescriptor(t, r);
					o && !("get" in o ? !t.__esModule : o.writable || o.configurable) || (o = {
						enumerable: !0,
						get: function() {
							return t[r]
						}
					}), Object.defineProperty(e, n, o)
				} : function(e, t, r, n) {
					void 0 === n && (n = r), e[n] = t[r]
				}),
				n = t && t.__setModuleDefault || (Object.create ? function(e, t) {
					Object.defineProperty(e, "default", {
						enumerable: !0,
						value: t
					})
				} : function(e, t) {
					e.default = t
				}),
				o = t && t.__importStar || function(e) {
					if (e && e.__esModule) return e;
					var t = {};
					if (null != e)
						for (var o in e) "default" !== o && Object.prototype.hasOwnProperty.call(e, o) && r(t, e, o);
					return n(t, e), t
				},
				i = t && t.__importDefault || function(e) {
					return e && e.__esModule ? e : {
						default: e
					}
				};
			Object.defineProperty(e, "__esModule", {
				value: !0
			}), e.PropsUtils = e.MicrofrontendContext = e.MicrofrontendProvider = e.withMicrofrontend = e.useShim = e.configShim = e.withPropsReceiver = void 0;
			var a = I;
			Object.defineProperty(e, "withPropsReceiver", {
				enumerable: !0,
				get: function() {
					return a.withPropsReceiver
				}
			}), Object.defineProperty(e, "configShim", {
				enumerable: !0,
				get: function() {
					return a.configShim
				}
			});
			var c = Q;
			Object.defineProperty(e, "useShim", {
				enumerable: !0,
				get: function() {
					return i(c).default
				}
			});
			var s = ee;
			Object.defineProperty(e, "withMicrofrontend", {
				enumerable: !0,
				get: function() {
					return s.withMicrofrontend
				}
			}), Object.defineProperty(e, "MicrofrontendProvider", {
				enumerable: !0,
				get: function() {
					return s.MicrofrontendProvider
				}
			}), Object.defineProperty(e, "MicrofrontendContext", {
				enumerable: !0,
				get: function() {
					return s.MicrofrontendContext
				}
			}), e.PropsUtils = o(xe)
		}(S);
	var Ee, Pe, De = {};
	({
		get exports() {
			return De
		},
		set exports(e) {
			De = e
		}
	}).exports = function() {
		if (Pe) return Ee;
		Pe = 1;
		var e = s ? c : (s = 1, c = "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED");

		function t() {}

		function r() {}
		return r.resetWarningCache = t, Ee = function() {
			function n(t, r, n, o, i, a) {
				if (a !== e) {
					var c = new Error("Calling PropTypes validators directly is not supported by the `prop-types` package. Use PropTypes.checkPropTypes() to call them. Read more at http://fb.me/use-check-prop-types");
					throw c.name = "Invariant Violation", c
				}
			}

			function o() {
				return n
			}
			n.isRequired = n;
			var i = {
				array: n,
				bigint: n,
				bool: n,
				func: n,
				number: n,
				object: n,
				string: n,
				symbol: n,
				any: n,
				arrayOf: o,
				element: n,
				elementType: n,
				instanceOf: o,
				node: n,
				objectOf: o,
				oneOf: o,
				oneOfType: o,
				shape: o,
				exact: o,
				checkPropTypes: r,
				resetWarningCache: t
			};
			return i.PropTypes = i, i
		}
	}()();
	const Ce = {
			width: "100%",
			height: "100%",
			padding: "2px",
			boxSizing: "border-box"
		},
		Ae = {
			width: "100%",
			height: "100%",
			position: "absolute",
			padding: "2px",
			boxSizing: "border-box"
		},
		Oe = {
			transition: "all 0.3s ease-out"
		},
		Be = "https://aem-discovery-stage.adobe.io",
		Re = "https://aem-discovery.adobe.io",
		Me = "aem-assets-frontend-1",
		ze = "9D0725C05E44FE1A0A49411C@AdobeOrg",
		Fe = "PROD",
		je = (e = {}, t = Fe) => {
			let r = (null == e ? void 0 : e.env) || t;
			r = r.toUpperCase();
			const n = "null" === (o = null == e ? void 0 : e.discoveryURL) || "undefined" === o || "" === o ? null : o;
			var o;
			const i = {
				...e,
				env: r,
				apiKey: (null == e ? void 0 : e.apiKey) || Me,
				imsOrg: (null == e ? void 0 : e.imsOrg) || ze,
				discoveryURL: n || (["DEV", "DEV_443", "QA", "STAGE"].includes(r) ? Be : Re)
			};
			return "PROD" === r && n === Be ? {
				...i,
				discoveryURL: Re
			} : i
		};
	var Le = [],
		Ue = [];

	function Ne(e, t) {
		if (e && "undefined" != typeof document) {
			var r, n = !0 === t.prepend ? "prepend" : "append",
				o = !0 === t.singleTag,
				i = "string" == typeof t.container ? document.querySelector(t.container) : document.getElementsByTagName("head")[0];
			if (o) {
				var a = Le.indexOf(i); - 1 === a && (a = Le.push(i) - 1, Ue[a] = {}), r = Ue[a] && Ue[a][n] ? Ue[a][n] : Ue[a][n] = c()
			} else r = c();
			65279 === e.charCodeAt(0) && (e = e.substring(1)), r.styleSheet ? r.styleSheet.cssText += e : r.appendChild(document.createTextNode(e))
		}

		function c() {
			var e = document.createElement("style");
			if (e.setAttribute("type", "text/css"), t.attributes)
				for (var r = Object.keys(t.attributes), o = 0; o < r.length; o++) e.setAttribute(r[o], t.attributes[r[o]]);
			var a = "prepend" === n ? "afterbegin" : "beforeend";
			return i.insertAdjacentElement(a, e), e
		}
	}
	var He = ".overlay {\n    position: absolute;\n    top: 0;\n    left: 0;\n    background-color: white;\n    width: 100%;\n    height: 100%;\n    z-index: 9999;\n    opacity: 0;\n}\n";
	Ne(He, {});
	const Ve = () => {
			const e = document.createElement("div");
			e.className = He.overlay || "overlay", e.setAttribute("data-testid", "dragOverlay"), document.body.appendChild(e)
		},
		qe = e => {
			try {
				document.getElementsByClassName(He.overlay || "overlay")[0].remove();
				const t = new DragEvent("drop", {
					bubbles: !0
				});
				Object.defineProperty(t, "dataTransfer", {
					value: {}
				}), t.dataTransfer.getData = t => {
					if (["collectionviewdata"].indexOf(t) > -1) return JSON.stringify(e.assets)
				};
				const r = document.elementFromPoint(e.mouse.x, e.mouse.y);
				Object.defineProperty(t, "target", {
					value: r
				}), r.dispatchEvent(t)
			} catch (e) {}
		},
		We = "@assets/selectors/AssetSelector",
		Ge = {
			rail: {
				type: De.bool,
				defaultValue: !1
			},
			discoveryURL: {
				type: De.string
			},
			imsOrg: {
				type: De.string,
				defaultValue: ze
			},
			imsToken: {
				type: De.string
			},
			apiKey: {
				type: De.string,
				defaultValue: Me
			},
			rootPath: {
				type: De.string
			},
			path: {
				type: De.string
			},
			filterSchema: {
				type: De.object
			},
			filterFormProps: {
				type: De.object
			},
			selectedAssets: {
				type: De.arrayOf(De.object)
			},
			acvConfig: {
				type: De.object
			},
			i18nSymbols: {
				type: De.objectOf(De.shape({
					id: De.string,
					defaultMessage: De.string,
					description: De.string
				}))
			},
			intl: {
				type: De.object
			},
			repositoryId: {
				type: De.string
			},
			additionalAemSolutions: {
				type: De.arrayOf(De.string)
			},
			noWrap: {
				type: De.bool,
				defaultValue: !1
			},
			dialogSize: {
				type: De.string,
				defaultValue: "fullscreen"
			},
			hideTreeNav: {
				type: De.bool,
				defaultValue: !1
			},
			onDrop: {
				type: De.func
			},
			dropOptions: {
				type: De.shape({
					allowList: De.object
				})
			},
			handleNavigateToAsset: {
				type: De.func
			},
			handleAssetSelection: {
				type: De.func
			},
			handleSelection: {
				type: De.func
			},
			onFilterSubmit: {
				type: De.func
			},
			onClose: {
				type: De.func
			},
			colorScheme: {
				type: De.string
			},
			env: {
				type: De.string,
				defaultValue: Fe
			},
			version: {
				type: De.string
			},
			statusScreenProps: {
				type: De.object
			},
			waitForImsToken: {
				type: De.bool,
				defaultValue: !1
			},
			infoPopoverMap: {
				type: De.func
			},
			dragStart: {
				type: De.func
			},
			dragEnd: {
				type: De.func
			}
		};
	S.PropsUtils.getPropTypes(Ge), S.PropsUtils.getDefaultProps(Ge);
	const Ke = e => {
			var t;
			const r = S.PropsUtils.useResolvedProps(e, Ge, We),
				n = S.useShim({
					frontend: "AssetSelector",
					serviceId: We,
					solutionName: "CQ-assets-selectors",
					env: r.env,
					version: r.version
				});
			(null === (t = r.acvConfig) || void 0 === t ? void 0 : t.dragOptions) && (r.dragStart = r.dragStart || Ve, r.dragEnd = r.dragEnd || qe);
			const o = je(r, null == r ? void 0 : r.env);
			return h.createElement(h.Fragment, null, h.createElement("div", {
				style: Ce,
				"data-testid": "asset-selector-shim-container"
			}, h.createElement(n, {
				...o
			})))
		},
		$e = e => {
			const t = {
					heading: "Waiting for user to authenticate...",
					description: " ",
					scale: 1
				},
				[r, n] = h.useState(null);
			return u.useEffect((() => {
				e.imsToken ? n(null) : n(t)
			}), [e.imsToken]), h.createElement(Ke, {
				...e,
				waitForImsToken: !0,
				statusScreenProps: r
			})
		};

	function Qe(e, t, r) {
		return t in e ? Object.defineProperty(e, t, {
			value: r,
			enumerable: !0,
			configurable: !0,
			writable: !0
		}) : e[t] = r, e
	}
	Ne('.spectrum-Button_e2d99e,.spectrum-ActionButton_e2d99e,.spectrum-LogicButton_e2d99e,.spectrum-FieldButton_e2d99e,.spectrum-ClearButton_e2d99e{font-family:adobe-clean-ux,adobe-clean,Source Sans Pro,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum-Button_e2d99e:lang(ar),.spectrum-ActionButton_e2d99e:lang(ar),.spectrum-LogicButton_e2d99e:lang(ar),.spectrum-FieldButton_e2d99e:lang(ar),.spectrum-ClearButton_e2d99e:lang(ar){font-family:adobe-arabic,myriad-arabic,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum-Button_e2d99e:lang(he),.spectrum-ActionButton_e2d99e:lang(he),.spectrum-LogicButton_e2d99e:lang(he),.spectrum-FieldButton_e2d99e:lang(he),.spectrum-ClearButton_e2d99e:lang(he){font-family:adobe-hebrew,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum-Button_e2d99e:lang(zh-Hans),.spectrum-ActionButton_e2d99e:lang(zh-Hans),.spectrum-LogicButton_e2d99e:lang(zh-Hans),.spectrum-FieldButton_e2d99e:lang(zh-Hans),.spectrum-ClearButton_e2d99e:lang(zh-Hans),.spectrum-Button_e2d99e:lang(zh),.spectrum-ActionButton_e2d99e:lang(zh),.spectrum-LogicButton_e2d99e:lang(zh),.spectrum-FieldButton_e2d99e:lang(zh),.spectrum-ClearButton_e2d99e:lang(zh){font-family:adobe-clean-han-simplified-c,SimSun,Heiti SC Light,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum-Button_e2d99e:lang(ko),.spectrum-ActionButton_e2d99e:lang(ko),.spectrum-LogicButton_e2d99e:lang(ko),.spectrum-FieldButton_e2d99e:lang(ko),.spectrum-ClearButton_e2d99e:lang(ko){font-family:adobe-clean-han-korean,Malgun Gothic,Apple Gothic,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum-Button_e2d99e:lang(ja),.spectrum-ActionButton_e2d99e:lang(ja),.spectrum-LogicButton_e2d99e:lang(ja),.spectrum-FieldButton_e2d99e:lang(ja),.spectrum-ClearButton_e2d99e:lang(ja){font-family:adobe-clean-han-japanese,Yu Gothic,メイリオ,ヒラギノ角ゴ Pro W3,Hiragino Kaku Gothic Pro W3,Osaka,ＭＳＰゴシック,MS PGothic,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum-Button_e2d99e,.spectrum-ActionButton_e2d99e,.spectrum-LogicButton_e2d99e,.spectrum-FieldButton_e2d99e,.spectrum-ClearButton_e2d99e{--spectrum-focus-ring-border-radius:var(--spectrum-textfield-border-radius,var(--spectrum-alias-border-radius-regular));--spectrum-focus-ring-gap:var(--spectrum-alias-input-focusring-gap);--spectrum-focus-ring-size:var(--spectrum-alias-input-focusring-size);--spectrum-focus-ring-border-size:0px;--spectrum-focus-ring-color:var(--spectrum-high-contrast-focus-ring-color,var(--spectrum-alias-focus-ring-color,var(--spectrum-alias-focus-color)))}.spectrum-Button_e2d99e:after,.spectrum-ActionButton_e2d99e:after,.spectrum-LogicButton_e2d99e:after,.spectrum-FieldButton_e2d99e:after,.spectrum-ClearButton_e2d99e:after{border-radius:calc(var(--spectrum-focus-ring-border-radius) + var(--spectrum-focus-ring-gap));content:"";margin:calc(-1*var(--spectrum-focus-ring-border-size));pointer-events:none;transition:box-shadow var(--spectrum-global-animation-duration-100,.13s)ease-out,margin var(--spectrum-global-animation-duration-100,.13s)ease-out;display:block;position:absolute;top:0;bottom:0;left:0;right:0}.spectrum-Button_e2d99e.focus-ring_e2d99e:after,.spectrum-ActionButton_e2d99e.focus-ring_e2d99e:after,.spectrum-LogicButton_e2d99e.focus-ring_e2d99e:after,.spectrum-FieldButton_e2d99e.focus-ring_e2d99e:after,.spectrum-ClearButton_e2d99e.focus-ring_e2d99e:after{margin:calc(var(--spectrum-focus-ring-gap)*-1 - var(--spectrum-focus-ring-border-size));box-shadow:0 0 0 var(--spectrum-focus-ring-size)var(--spectrum-focus-ring-color)}.spectrum-FieldButton--quiet_e2d99e:after{border-radius:0}.spectrum-FieldButton--quiet_e2d99e.focus-ring_e2d99e:after{margin:0 0 calc(var(--spectrum-focus-ring-gap)*-1 - var(--spectrum-focus-ring-border-size))0;box-shadow:0 var(--spectrum-focus-ring-size)0 var(--spectrum-focus-ring-color)}.spectrum-Button_e2d99e,.spectrum-ActionButton_e2d99e,.spectrum-LogicButton_e2d99e,.spectrum-FieldButton_e2d99e,.spectrum-ClearButton_e2d99e{box-sizing:border-box;border-radius:var(--spectrum-button-border-radius);border-style:solid;border-width:var(--spectrum-button-border-width);--spectrum-focus-ring-border-radius:var(--spectrum-button-border-radius);--spectrum-focus-ring-border-size:var(--spectrum-button-border-width);--spectrum-focus-ring-gap:var(--spectrum-alias-focus-ring-gap,var(--spectrum-global-dimension-static-size-25));--spectrum-focus-ring-size:var(--spectrum-button-primary-focus-ring-size-key-focus,var(--spectrum-alias-focus-ring-size));text-transform:none;-webkit-font-smoothing:antialiased;-moz-osx-font-smoothing:grayscale;vertical-align:top;transition:background var(--spectrum-global-animation-duration-100,.13s)ease-out,border-color var(--spectrum-global-animation-duration-100,.13s)ease-out,color var(--spectrum-global-animation-duration-100,.13s)ease-out,box-shadow var(--spectrum-global-animation-duration-100,.13s)ease-out;-webkit-user-select:none;user-select:none;-ms-touch-action:none;touch-action:none;cursor:default;isolation:isolate;justify-content:center;align-items:center;margin:0;line-height:1.3;text-decoration:none;display:inline-flex;position:relative;overflow:visible}button.spectrum-Button_e2d99e,button.spectrum-ActionButton_e2d99e,button.spectrum-LogicButton_e2d99e,button.spectrum-FieldButton_e2d99e,button.spectrum-ClearButton_e2d99e{-webkit-appearance:button}.spectrum-Button_e2d99e:focus,.spectrum-ActionButton_e2d99e:focus,.spectrum-LogicButton_e2d99e:focus,.spectrum-FieldButton_e2d99e:focus,.spectrum-ClearButton_e2d99e:focus{outline:none}.spectrum-Button_e2d99e.focus-ring_e2d99e,.spectrum-ActionButton_e2d99e.focus-ring_e2d99e,.spectrum-LogicButton_e2d99e.focus-ring_e2d99e,.spectrum-FieldButton_e2d99e.focus-ring_e2d99e,.spectrum-ClearButton_e2d99e.focus-ring_e2d99e{z-index:3}.spectrum-Button_e2d99e::-moz-focus-inner,.spectrum-ActionButton_e2d99e::-moz-focus-inner,.spectrum-LogicButton_e2d99e::-moz-focus-inner,.spectrum-FieldButton_e2d99e::-moz-focus-inner,.spectrum-ClearButton_e2d99e::-moz-focus-inner{border:0;margin-top:-2px;margin-bottom:-2px;padding:0}.spectrum-Button_e2d99e:disabled,.spectrum-ActionButton_e2d99e:disabled,.spectrum-LogicButton_e2d99e:disabled,.spectrum-FieldButton_e2d99e:disabled,.spectrum-ClearButton_e2d99e:disabled{cursor:default}.spectrum-Button_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton_e2d99e .spectrum-Icon_e2d99e,.spectrum-LogicButton_e2d99e .spectrum-Icon_e2d99e,.spectrum-FieldButton_e2d99e .spectrum-Icon_e2d99e,.spectrum-ClearButton_e2d99e .spectrum-Icon_e2d99e{max-height:100%;transition:background var(--spectrum-global-animation-duration-100,.13s)ease-out,fill var(--spectrum-global-animation-duration-100,.13s)ease-out;box-sizing:initial;flex-shrink:0;order:0}.spectrum-Button_e2d99e{--spectrum-button-border-radius:var(--spectrum-button-primary-border-radius,var(--spectrum-alias-border-radius-large));--spectrum-button-border-width:var(--spectrum-button-primary-border-size,var(--spectrum-alias-border-size-thick));min-height:var(--spectrum-button-primary-height,var(--spectrum-alias-single-line-height));height:0%;min-width:var(--spectrum-button-primary-min-width);padding:var(--spectrum-global-dimension-size-50)calc(var(--spectrum-button-primary-padding-x,var(--spectrum-global-dimension-size-200)) - var(--spectrum-button-primary-border-size,var(--spectrum-alias-border-size-thick)));padding-bottom:calc(var(--spectrum-global-dimension-size-50) + 1px);padding-top:calc(var(--spectrum-global-dimension-size-50) - 1px);font-size:var(--spectrum-button-primary-text-size,var(--spectrum-alias-pill-button-text-size));font-weight:var(--spectrum-button-primary-text-font-weight,var(--spectrum-global-font-weight-bold));border-style:solid}.spectrum-Button_e2d99e.is-hovered_e2d99e,.spectrum-Button_e2d99e:active{box-shadow:none}[dir=ltr] .spectrum-Button_e2d99e .spectrum-Icon_e2d99e+.spectrum-Button-label_e2d99e{margin-left:var(--spectrum-button-primary-text-gap,var(--spectrum-global-dimension-size-100))}[dir=rtl] .spectrum-Button_e2d99e .spectrum-Icon_e2d99e+.spectrum-Button-label_e2d99e,[dir=ltr] .spectrum-Button_e2d99e .spectrum-Button-label_e2d99e+.spectrum-Icon_e2d99e{margin-right:var(--spectrum-button-primary-text-gap,var(--spectrum-global-dimension-size-100))}[dir=rtl] .spectrum-Button_e2d99e .spectrum-Button-label_e2d99e+.spectrum-Icon_e2d99e{margin-left:var(--spectrum-button-primary-text-gap,var(--spectrum-global-dimension-size-100))}.spectrum-Button_e2d99e.spectrum-Button--iconOnly_e2d99e{min-width:unset;padding:var(--spectrum-global-dimension-size-65)}a.spectrum-Button_e2d99e,a.spectrum-ActionButton_e2d99e{-webkit-appearance:none;-webkit-user-select:none;user-select:none;cursor:pointer}.spectrum-ActionButton_e2d99e{height:var(--spectrum-actionbutton-height,var(--spectrum-alias-single-line-height));min-width:var(--spectrum-actionbutton-min-width,var(--spectrum-global-dimension-size-400));--spectrum-button-border-radius:var(--spectrum-actionbutton-border-radius,var(--spectrum-alias-border-radius-regular));--spectrum-button-border-width:var(--spectrum-actionbutton-border-size,var(--spectrum-alias-border-size-thin));font-size:var(--spectrum-actionbutton-text-size,var(--spectrum-alias-font-size-default));font-weight:var(--spectrum-actionbutton-text-font-weight,var(--spectrum-alias-body-text-font-weight));padding:0;position:relative}[dir=ltr] .spectrum-ActionButton_e2d99e .spectrum-Icon_e2d99e{padding-left:var(--spectrum-actionbutton-icon-padding-x,var(--spectrum-global-dimension-size-85))}[dir=rtl] .spectrum-ActionButton_e2d99e .spectrum-Icon_e2d99e{padding-right:var(--spectrum-actionbutton-icon-padding-x,var(--spectrum-global-dimension-size-85))}[dir=ltr] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-label_e2d99e{padding-right:var(--spectrum-actionbutton-text-padding-x,var(--spectrum-global-dimension-size-150))}[dir=rtl] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-label_e2d99e{padding-left:var(--spectrum-actionbutton-text-padding-x,var(--spectrum-global-dimension-size-150))}[dir=ltr] .spectrum-ActionButton_e2d99e .spectrum-Icon_e2d99e+.spectrum-ActionButton-label_e2d99e{padding-left:var(--spectrum-actionbutton-icon-padding-x,var(--spectrum-global-dimension-size-85))}[dir=rtl] .spectrum-ActionButton_e2d99e .spectrum-Icon_e2d99e+.spectrum-ActionButton-label_e2d99e,[dir=ltr] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-label_e2d99e:not([hidden])+.spectrum-Icon_e2d99e{padding-right:var(--spectrum-actionbutton-icon-padding-x,var(--spectrum-global-dimension-size-85))}[dir=rtl] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-label_e2d99e:not([hidden])+.spectrum-Icon_e2d99e{padding-left:var(--spectrum-actionbutton-icon-padding-x,var(--spectrum-global-dimension-size-85))}[dir=ltr] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-label_e2d99e:only-child,[dir=ltr] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-hold_e2d99e+.spectrum-ActionButton-label_e2d99e:last-child{padding-left:var(--spectrum-actionbutton-text-padding-x,var(--spectrum-global-dimension-size-150))}[dir=rtl] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-label_e2d99e:only-child,[dir=rtl] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-hold_e2d99e+.spectrum-ActionButton-label_e2d99e:last-child{padding-right:var(--spectrum-actionbutton-text-padding-x,var(--spectrum-global-dimension-size-150))}[dir=ltr] .spectrum-ActionButton_e2d99e .spectrum-Icon_e2d99e:only-child,[dir=ltr] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-hold_e2d99e+.spectrum-Icon_e2d99e:last-child{padding-right:var(--spectrum-actionbutton-icon-padding-x,var(--spectrum-global-dimension-size-85))}[dir=rtl] .spectrum-ActionButton_e2d99e .spectrum-Icon_e2d99e:only-child,[dir=rtl] .spectrum-ActionButton_e2d99e .spectrum-ActionButton-hold_e2d99e+.spectrum-Icon_e2d99e:last-child{padding-left:var(--spectrum-actionbutton-icon-padding-x,var(--spectrum-global-dimension-size-85))}[dir=ltr] .spectrum-ActionButton-hold_e2d99e{right:var(--spectrum-actionbutton-hold-icon-padding-right,var(--spectrum-global-dimension-size-40))}[dir=rtl] .spectrum-ActionButton-hold_e2d99e{left:var(--spectrum-actionbutton-hold-icon-padding-right,var(--spectrum-global-dimension-size-40))}.spectrum-ActionButton-hold_e2d99e{bottom:var(--spectrum-actionbutton-hold-icon-padding-bottom,var(--spectrum-global-dimension-size-40));position:absolute}[dir=rtl] .spectrum-ActionButton-hold_e2d99e{transform:rotate(90deg)}.spectrum-ActionButton-label_e2d99e,.spectrum-Button-label_e2d99e{-ms-grid-row-align:center;text-align:center;width:100%;order:1;place-self:center}.spectrum-ActionButton-label_e2d99e:empty,.spectrum-Button-label_e2d99e:empty{display:none}.spectrum-ActionButton-label_e2d99e{white-space:nowrap;text-overflow:ellipsis;overflow:hidden}.spectrum-ActionButton--quiet_e2d99e{border-width:var(--spectrum-actionbutton-quiet-border-size,var(--spectrum-alias-border-size-thin));border-radius:var(--spectrum-actionbutton-quiet-border-radius,var(--spectrum-alias-border-radius-regular));font-size:var(--spectrum-actionbutton-quiet-text-size,var(--spectrum-alias-font-size-default));font-weight:var(--spectrum-actionbutton-quiet-text-font-weight,var(--spectrum-alias-body-text-font-weight))}.spectrum-LogicButton_e2d99e{height:var(--spectrum-logicbutton-and-height,24px);padding:var(--spectrum-logicbutton-and-padding-x,var(--spectrum-global-dimension-size-100));--spectrum-button-border-width:var(--spectrum-logicbutton-and-border-size,var(--spectrum-alias-border-size-thick));--spectrum-button-border-radius:var(--spectrum-logicbutton-and-border-radius,var(--spectrum-alias-border-radius-regular));font-size:var(--spectrum-logicbutton-and-text-size,var(--spectrum-alias-font-size-default));font-weight:var(--spectrum-logicbutton-and-text-font-weight,var(--spectrum-global-font-weight-bold));line-height:0}.spectrum-FieldButton_e2d99e{height:var(--spectrum-dropdown-height,var(--spectrum-global-dimension-size-400));padding:0 var(--spectrum-dropdown-padding-x,var(--spectrum-global-dimension-size-150));font-family:inherit;font-weight:400;font-size:var(--spectrum-dropdown-text-size,var(--spectrum-alias-font-size-default));-webkit-font-smoothing:initial;cursor:default;--spectrum-focus-ring-gap:var(--spectrum-alias-input-focusring-gap);--spectrum-focus-ring-size:var(--spectrum-alias-input-focusring-size);padding-top:0;padding-bottom:0;padding-left:var(--spectrum-dropdown-padding-x,var(--spectrum-global-dimension-size-150));padding-right:var(--spectrum-dropdown-padding-x,var(--spectrum-global-dimension-size-150));--spectrum-button-border-width:var(--spectrum-dropdown-border-size,var(--spectrum-alias-border-size-thin));--spectrum-button-border-radius:var(--spectrum-alias-border-radius-regular,var(--spectrum-global-dimension-size-50));transition:background-color var(--spectrum-global-animation-duration-100,.13s),box-shadow var(--spectrum-global-animation-duration-100,.13s),border-color var(--spectrum-global-animation-duration-100,.13s);border-style:solid;outline:none;margin:0;line-height:normal;position:relative}.spectrum-FieldButton_e2d99e:disabled,.spectrum-FieldButton_e2d99e.is-disabled_e2d99e{cursor:default;border-width:0}.spectrum-FieldButton_e2d99e.is-open_e2d99e{border-width:var(--spectrum-dropdown-border-size,var(--spectrum-alias-border-size-thin))}.spectrum-FieldButton--quiet_e2d99e{--spectrum-button-border-width:0;--spectrum-button-border-radius:var(--spectrum-fieldbutton-quiet-border-radius,0px);--spectrum-focus-ring-size:var(--spectrum-alias-focus-ring-size,var(--spectrum-global-dimension-static-size-25));margin:0;padding:0}.spectrum-FieldButton--quiet_e2d99e:disabled.focus-ring_e2d99e,.spectrum-FieldButton--quiet_e2d99e.is-disabled_e2d99e.focus-ring_e2d99e{box-shadow:none}.spectrum-ClearButton_e2d99e{width:var(--spectrum-clearbutton-medium-width,var(--spectrum-alias-single-line-height));height:var(--spectrum-clearbutton-medium-height,var(--spectrum-alias-single-line-height));--spectrum-button-border-radius:100%;--spectrum-button-border-width:0px;border:none;margin:0;padding:0}.spectrum-ClearButton_e2d99e>.spectrum-Icon_e2d99e{margin:0 auto}@media screen and (-ms-high-contrast:active),(-ms-high-contrast:none){.spectrum-ClearButton_e2d99e>.spectrum-Icon_e2d99e{margin:0}}.spectrum-ClearButton--small_e2d99e{width:var(--spectrum-clearbutton-small-width,var(--spectrum-global-dimension-size-300));height:var(--spectrum-clearbutton-small-height,var(--spectrum-global-dimension-size-300))}.spectrum-ClearButton_e2d99e{background-color:var(--spectrum-clearbutton-medium-background-color,var(--spectrum-alias-background-color-transparent));color:var(--spectrum-clearbutton-medium-icon-color,var(--spectrum-alias-icon-color))}.spectrum-ClearButton_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-clearbutton-medium-icon-color,var(--spectrum-alias-icon-color))}.spectrum-ClearButton_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-clearbutton-medium-background-color-hover,var(--spectrum-alias-background-color-transparent));color:var(--spectrum-clearbutton-medium-icon-color-hover,var(--spectrum-alias-icon-color-hover))}.spectrum-ClearButton_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-clearbutton-medium-icon-color-hover,var(--spectrum-alias-icon-color-hover))}.spectrum-ClearButton_e2d99e.is-active_e2d99e{background-color:var(--spectrum-clearbutton-medium-background-color-down,var(--spectrum-alias-background-color-transparent));color:var(--spectrum-clearbutton-medium-icon-color-down,var(--spectrum-alias-icon-color-down))}.spectrum-ClearButton_e2d99e.is-active_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-clearbutton-medium-icon-color-down,var(--spectrum-alias-icon-color-down))}.spectrum-ClearButton_e2d99e.focus-ring_e2d99e{background-color:var(--spectrum-clearbutton-medium-background-color-key-focus,var(--spectrum-alias-background-color-transparent));color:var(--spectrum-clearbutton-medium-icon-color-key-focus,var(--spectrum-alias-icon-color-focus))}.spectrum-ClearButton_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-clearbutton-medium-icon-color-key-focus,var(--spectrum-alias-icon-color-focus))}.spectrum-ClearButton_e2d99e:disabled,.spectrum-ClearButton_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-clearbutton-medium-background-color-disabled,var(--spectrum-alias-background-color-transparent));color:var(--spectrum-clearbutton-medium-icon-color-disabled,var(--spectrum-alias-icon-color-disabled))}.spectrum-ClearButton_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-ClearButton_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-clearbutton-medium-icon-color-disabled,var(--spectrum-alias-icon-color-disabled))}.spectrum-Button_e2d99e[data-style=fill]{--spectrum-button-text-color:white;--spectrum-button-text-color-hover:var(--spectrum-button-text-color);--spectrum-button-text-color-down:var(--spectrum-button-text-color);--spectrum-button-text-color-key-focus:var(--spectrum-button-text-color);--spectrum-button-text-color-disabled:var(--spectrum-alias-text-color-disabled,var(--spectrum-global-color-gray-500));--spectrum-button-color-disabled:var(--spectrum-alias-background-color-disabled);background-color:var(--spectrum-high-contrast-button-text,var(--spectrum-button-color));color:var(--spectrum-high-contrast-button-face,var(--spectrum-button-text-color));border-color:#0000}.spectrum-Button_e2d99e[data-style=fill].is-hovered_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-button-color-hover));color:var(--spectrum-high-contrast-button-face,var(--spectrum-button-text-color-hover))}.spectrum-Button_e2d99e[data-style=fill].focus-ring_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-button-color-key-focus));color:var(--spectrum-high-contrast-button-face,var(--spectrum-button-text-color-key-focus))}.spectrum-Button_e2d99e[data-style=fill].is-active_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-button-color-down));color:var(--spectrum-high-contrast-button-face,var(--spectrum-button-text-color-down))}.spectrum-Button_e2d99e[data-style=fill]:disabled,.spectrum-Button_e2d99e[data-style=fill].is-disabled_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-button-color-disabled));color:var(--spectrum-high-contrast-gray-text,var(--spectrum-button-text-color-disabled))}.spectrum-Button_e2d99e[data-style=outline]{--spectrum-button-text-color:var(--spectrum-button-color);--spectrum-button-text-color-hover:var(--spectrum-button-color-hover);--spectrum-button-text-color-down:var(--spectrum-button-color-down);--spectrum-button-text-color-key-focus:var(--spectrum-button-color-key-focus);--spectrum-button-text-color-disabled:var(--spectrum-alias-text-color-disabled,var(--spectrum-global-color-gray-500));--spectrum-button-color-disabled:var(--spectrum-alias-background-color-disabled);border-color:var(--spectrum-high-contrast-button-text,var(--spectrum-button-color));color:var(--spectrum-high-contrast-button-text,var(--spectrum-button-text-color));background-color:#0000}.spectrum-Button_e2d99e[data-style=outline].is-hovered_e2d99e{background-color:var(--spectrum-high-contrast-transparent,var(--spectrum-button-background-color-hover));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-button-color-hover));color:var(--spectrum-high-contrast-button-text,var(--spectrum-button-text-color-hover))}.spectrum-Button_e2d99e[data-style=outline].focus-ring_e2d99e{background-color:var(--spectrum-high-contrast-transparent,var(--spectrum-button-background-color-key-focus));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-button-color-key-focus));color:var(--spectrum-high-contrast-button-text,var(--spectrum-button-text-color-key-focus))}.spectrum-Button_e2d99e[data-style=outline].is-active_e2d99e{background-color:var(--spectrum-high-contrast-transparent,var(--spectrum-button-background-color-down));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-button-color-down));color:var(--spectrum-high-contrast-button-text,var(--spectrum-button-text-color-down))}.spectrum-Button_e2d99e[data-style=outline]:disabled,.spectrum-Button_e2d99e[data-style=outline].is-disabled_e2d99e{border-color:var(--spectrum-high-contrast-gray-text,var(--spectrum-button-color-disabled));color:var(--spectrum-high-contrast-gray-text,var(--spectrum-button-text-color-disabled));background-color:#0000}.spectrum-Button_e2d99e[data-static-color=white]{--spectrum-focus-ring-color:white}.spectrum-Button_e2d99e[data-static-color=white][data-variant=accent][data-style=fill],.spectrum-Button_e2d99e[data-static-color=white][data-variant=negative][data-style=fill],.spectrum-Button_e2d99e[data-static-color=white][data-variant=primary][data-style=fill]{--spectrum-button-color:#ffffffe6;--spectrum-button-color-hover:white;--spectrum-button-color-down:white;--spectrum-button-color-key-focus:white;--spectrum-button-color-disabled:#ffffff1a;--spectrum-button-text-color:black;--spectrum-button-text-color-disabled:#ffffff8c}.spectrum-Button_e2d99e[data-static-color=white][data-variant=accent][data-style=outline],.spectrum-Button_e2d99e[data-static-color=white][data-variant=negative][data-style=outline],.spectrum-Button_e2d99e[data-static-color=white][data-variant=primary][data-style=outline]{--spectrum-button-color:#ffffffe6;--spectrum-button-color-hover:white;--spectrum-button-color-down:white;--spectrum-button-color-key-focus:white;--spectrum-button-color-disabled:#ffffff40;--spectrum-button-text-color:white;--spectrum-button-text-color-hover:white;--spectrum-button-text-color-down:white;--spectrum-button-text-color-key-focus:white;--spectrum-button-text-color-disabled:#ffffff8c;--spectrum-button-background-color-hover:#ffffff1a;--spectrum-button-background-color-down:#ffffff26;--spectrum-button-background-color-key-focus:#ffffff1a}.spectrum-Button_e2d99e[data-static-color=white][data-variant=secondary][data-style=fill]{--spectrum-button-color:#ffffff12;--spectrum-button-color-hover:#ffffff1a;--spectrum-button-color-down:#ffffff26;--spectrum-button-color-key-focus:#ffffff1a;--spectrum-button-color-disabled:#ffffff1a;--spectrum-button-text-color:white;--spectrum-button-text-color-disabled:#ffffff8c}.spectrum-Button_e2d99e[data-static-color=white][data-variant=secondary][data-style=outline]{--spectrum-button-color:#ffffff40;--spectrum-button-color-hover:#fff6;--spectrum-button-color-down:#ffffff8c;--spectrum-button-color-key-focus:#fff6;--spectrum-button-color-disabled:#ffffff40;--spectrum-button-text-color:white;--spectrum-button-text-color-hover:white;--spectrum-button-text-color-down:white;--spectrum-button-text-color-key-focus:white;--spectrum-button-text-color-disabled:#ffffff8c;--spectrum-button-background-color-hover:#ffffff1a;--spectrum-button-background-color-down:#ffffff26;--spectrum-button-background-color-key-focus:#ffffff1a}.spectrum-Button_e2d99e[data-static-color=black]{--spectrum-focus-ring-color:black}.spectrum-Button_e2d99e[data-static-color=black][data-variant=accent][data-style=fill],.spectrum-Button_e2d99e[data-static-color=black][data-variant=negative][data-style=fill],.spectrum-Button_e2d99e[data-static-color=black][data-variant=primary][data-style=fill]{--spectrum-button-color:#000000e6;--spectrum-button-color-hover:black;--spectrum-button-color-down:black;--spectrum-button-color-key-focus:black;--spectrum-button-color-disabled:#0000001a;--spectrum-button-text-color:white;--spectrum-button-text-color-disabled:#0000008c}.spectrum-Button_e2d99e[data-static-color=black][data-variant=accent][data-style=outline],.spectrum-Button_e2d99e[data-static-color=black][data-variant=negative][data-style=outline],.spectrum-Button_e2d99e[data-static-color=black][data-variant=primary][data-style=outline]{--spectrum-button-color:#000000e6;--spectrum-button-color-hover:black;--spectrum-button-color-down:black;--spectrum-button-color-key-focus:black;--spectrum-button-color-disabled:#00000040;--spectrum-button-text-color:black;--spectrum-button-text-color-hover:black;--spectrum-button-text-color-down:black;--spectrum-button-text-color-key-focus:black;--spectrum-button-text-color-disabled:#0000008c;--spectrum-button-background-color-hover:#0000001a;--spectrum-button-background-color-down:#00000026;--spectrum-button-background-color-key-focus:#0000001a}.spectrum-Button_e2d99e[data-static-color=black][data-variant=secondary][data-style=fill]{--spectrum-button-color:#00000012;--spectrum-button-color-hover:#0000001a;--spectrum-button-color-down:#00000026;--spectrum-button-color-key-focus:#0000001a;--spectrum-button-color-disabled:#0000001a;--spectrum-button-text-color:black;--spectrum-button-text-color-disabled:#0000008c}.spectrum-Button_e2d99e[data-static-color=black][data-variant=secondary][data-style=outline]{--spectrum-button-color:#00000040;--spectrum-button-color-hover:#0006;--spectrum-button-color-down:#0000008c;--spectrum-button-color-key-focus:#0006;--spectrum-button-color-disabled:#00000040;--spectrum-button-text-color:black;--spectrum-button-text-color-hover:black;--spectrum-button-text-color-down:black;--spectrum-button-text-color-key-focus:black;--spectrum-button-text-color-disabled:#0000008c;--spectrum-button-background-color-hover:#0000001a;--spectrum-button-background-color-down:#00000026;--spectrum-button-background-color-key-focus:#0000001a}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=accent][data-style=fill]{--spectrum-button-color:var(--spectrum-accent-background-color-default);--spectrum-button-color-hover:var(--spectrum-accent-background-color-hover);--spectrum-button-color-down:var(--spectrum-accent-background-color-down);--spectrum-button-color-key-focus:var(--spectrum-accent-background-color-key-focus)}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=accent][data-style=outline]{--spectrum-button-color:var(--spectrum-accent-color-900);--spectrum-button-color-hover:var(--spectrum-accent-color-1000);--spectrum-button-color-down:var(--spectrum-accent-color-1100);--spectrum-button-color-key-focus:var(--spectrum-accent-color-1000);--spectrum-button-background-color-hover:var(--spectrum-accent-color-200);--spectrum-button-background-color-down:var(--spectrum-accent-color-300);--spectrum-button-background-color-key-focus:var(--spectrum-accent-color-200)}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=negative][data-style=fill]{--spectrum-button-color:var(--spectrum-negative-background-color-default);--spectrum-button-color-hover:var(--spectrum-negative-background-color-hover);--spectrum-button-color-down:var(--spectrum-negative-background-color-down);--spectrum-button-color-key-focus:var(--spectrum-negative-background-color-key-focus)}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=negative][data-style=outline]{--spectrum-button-color:var(--spectrum-red-900);--spectrum-button-color-hover:var(--spectrum-red-1000);--spectrum-button-color-down:var(--spectrum-red-1100);--spectrum-button-color-key-focus:var(--spectrum-red-1000);--spectrum-button-background-color-hover:var(--spectrum-red-200);--spectrum-button-background-color-down:var(--spectrum-red-300);--spectrum-button-background-color-key-focus:var(--spectrum-red-200)}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=primary][data-style=fill]{--spectrum-button-color:var(--spectrum-neutral-background-color-default);--spectrum-button-color-hover:var(--spectrum-neutral-background-color-hover);--spectrum-button-color-down:var(--spectrum-neutral-background-color-down);--spectrum-button-color-key-focus:var(--spectrum-neutral-background-color-key-focus)}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=primary][data-style=outline]{--spectrum-button-color:var(--spectrum-gray-800);--spectrum-button-color-hover:var(--spectrum-gray-900);--spectrum-button-color-down:var(--spectrum-gray-900);--spectrum-button-color-key-focus:var(--spectrum-gray-900);--spectrum-button-background-color-hover:var(--spectrum-gray-300);--spectrum-button-background-color-down:var(--spectrum-gray-400);--spectrum-button-background-color-key-focus:var(--spectrum-gray-300)}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=secondary]{--spectrum-button-text-color:var(--spectrum-gray-800);--spectrum-button-text-color-hover:var(--spectrum-gray-900);--spectrum-button-text-color-down:var(--spectrum-gray-900);--spectrum-button-text-color-key-focus:var(--spectrum-gray-900)}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=secondary][data-style=fill]{--spectrum-button-color:var(--spectrum-gray-200);--spectrum-button-color-hover:var(--spectrum-gray-300);--spectrum-button-color-down:var(--spectrum-gray-400);--spectrum-button-color-key-focus:var(--spectrum-gray-300)}.spectrum-Button_e2d99e:not([data-static-color])[data-variant=secondary][data-style=outline]{--spectrum-button-color:var(--spectrum-gray-300);--spectrum-button-color-hover:var(--spectrum-gray-400);--spectrum-button-color-down:var(--spectrum-gray-500);--spectrum-button-color-key-focus:var(--spectrum-gray-400);--spectrum-button-background-color-hover:var(--spectrum-gray-300);--spectrum-button-background-color-down:var(--spectrum-gray-400);--spectrum-button-background-color-key-focus:var(--spectrum-gray-300)}@media (forced-colors:active){.spectrum-Button_e2d99e,.spectrum-ActionButton_e2d99e{forced-color-adjust:none;--spectrum-high-contrast-transparent:transparent;--spectrum-high-contrast-button-face:ButtonFace;--spectrum-high-contrast-button-text:ButtonText;--spectrum-high-contrast-highlight:Highlight;--spectrum-high-contrast-highlight-text:HighlightText;--spectrum-high-contrast-gray-text:GrayText;--spectrum-high-contrast-focus-ring-color:var(--spectrum-high-contrast-button-text)}}.spectrum-ActionButton_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-background-color,var(--spectrum-global-color-gray-75)));border-color:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-border-color,var(--spectrum-alias-border-color)));color:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-text-color,var(--spectrum-alias-text-color)))}.spectrum-ActionButton_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-icon-color,var(--spectrum-alias-icon-color)))}.spectrum-ActionButton_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-hold-icon-color,var(--spectrum-alias-icon-color)))}.spectrum-ActionButton_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-background-color-hover,var(--spectrum-global-color-gray-50)));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-actionbutton-border-color-hover,var(--spectrum-alias-border-color-hover)));color:var(--spectrum-actionbutton-text-color-hover,var(--spectrum-alias-text-color-hover))}.spectrum-ActionButton_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-icon-color-hover,var(--spectrum-alias-icon-color-hover)))}.spectrum-ActionButton_e2d99e.is-hovered_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-actionbutton-hold-icon-color-hover,var(--spectrum-alias-icon-color-hover))}.spectrum-ActionButton_e2d99e.focus-ring_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-background-color-key-focus,var(--spectrum-global-color-gray-50)));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-actionbutton-border-color-hover,var(--spectrum-alias-border-color-hover)));color:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-text-color-key-focus,var(--spectrum-alias-text-color-hover)))}.spectrum-ActionButton_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-icon-color-key-focus,var(--spectrum-alias-icon-color-focus)))}.spectrum-ActionButton_e2d99e.focus-ring_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-hold-icon-color-key-focus,var(--spectrum-alias-icon-color-hover)))}.spectrum-ActionButton_e2d99e.is-active_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-background-color-down,var(--spectrum-global-color-gray-200)));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-actionbutton-border-color-down,var(--spectrum-alias-border-color-down)));color:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-text-color-down,var(--spectrum-alias-text-color-down)))}.spectrum-ActionButton_e2d99e.is-active_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-hold-icon-color-down,var(--spectrum-alias-icon-color-down)))}.spectrum-ActionButton_e2d99e:disabled,.spectrum-ActionButton_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-background-color-disabled,var(--spectrum-global-color-gray-200)));border-color:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-border-color-disabled,var(--spectrum-alias-border-color-disabled)));color:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-text-color-disabled,var(--spectrum-alias-text-color-disabled)))}.spectrum-ActionButton_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-ActionButton_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-icon-color-disabled,var(--spectrum-alias-icon-color-disabled)))}.spectrum-ActionButton_e2d99e:disabled .spectrum-ActionButton-hold_e2d99e,.spectrum-ActionButton_e2d99e.is-disabled_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-hold-icon-color-disabled,var(--spectrum-alias-icon-color-disabled)))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-alias-toggle-color-selected));border-color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-alias-toggle-color-selected));color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-gray-50))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-highlight-text,var(--spectrum-gray-50))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-alias-toggle-color-selected-hover));border-color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-alias-toggle-color-selected-hover));color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-gray-50))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-highlight-text,var(--spectrum-gray-50))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e.focus-ring_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-alias-toggle-color-selected-key-focus));border-color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-alias-toggle-color-selected-key-focus));color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-gray-50))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-highlight-text,var(--spectrum-gray-50))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e.is-active_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-alias-toggle-color-selected-down));border-color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-alias-toggle-color-selected-down));color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-gray-50))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e.is-active_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-highlight-text,var(--spectrum-gray-50))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e:disabled,.spectrum-ActionButton_e2d99e.is-selected_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-background-color-selected-disabled,var(--spectrum-global-color-gray-200)));border-color:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-border-color-selected-disabled,var(--spectrum-alias-border-color-disabled)));color:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-text-color-selected-disabled,var(--spectrum-alias-text-color-disabled)))}.spectrum-ActionButton_e2d99e.is-selected_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-ActionButton_e2d99e.is-selected_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-icon-color-selected-disabled,var(--spectrum-alias-icon-color-disabled)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-accent-background-color-default));border-color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-accent-background-color-default));color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-actionbutton-emphasized-text-color-selected,var(--spectrum-global-color-static-white)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-highlight-text,var(--spectrum-actionbutton-emphasized-icon-color-selected,var(--spectrum-global-color-static-white)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.focus-ring_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e.focus-ring_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-accent-background-color-key-focus));border-color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-accent-background-color-hover));color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-actionbutton-emphasized-text-color-selected-key-focus,var(--spectrum-global-color-static-white)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-highlight-text,var(--spectrum-actionbutton-emphasized-icon-color-selected-key-focus,var(--spectrum-global-color-static-white)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-hovered_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-accent-background-color-hover));border-color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-accent-background-color-hover));color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-actionbutton-emphasized-text-color-selected-hover,var(--spectrum-global-color-static-white)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-highlight-text,var(--spectrum-actionbutton-emphasized-icon-color-selected-hover,var(--spectrum-global-color-static-white)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-active_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e.is-active_e2d99e{background-color:var(--spectrum-high-contrast-highlight,var(--spectrum-accent-background-color-down));border-color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-accent-background-color-down));color:var(--spectrum-high-contrast-highlight-text,var(--spectrum-actionbutton-emphasized-text-color-selected-down,var(--spectrum-global-color-static-white)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-active_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e.is-active_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-highlight-text,var(--spectrum-actionbutton-emphasized-icon-color-selected-down,var(--spectrum-global-color-static-white)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e:disabled,.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-disabled_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e:disabled,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-emphasized-background-color-selected-disabled,var(--spectrum-global-color-gray-200)));border-color:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-emphasized-border-color-selected-disabled,var(--spectrum-alias-border-color-disabled)));color:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-emphasized-text-color-selected-disabled,var(--spectrum-alias-text-color-disabled)))}.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-ActionButton--emphasized_e2d99e.is-selected_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-emphasized-icon-color-selected-disabled,var(--spectrum-alias-icon-color-disabled)))}.spectrum-ActionButton--quiet_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-quiet-background-color,var(--spectrum-alias-background-color-transparent)));border-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-quiet-border-color,var(--spectrum-alias-border-color-transparent)));color:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-quiet-text-color,var(--spectrum-alias-text-color)))}.spectrum-ActionButton--quiet_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-quiet-background-color-hover,var(--spectrum-alias-background-color-transparent)));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-actionbutton-quiet-border-color-hover,var(--spectrum-alias-border-color-transparent)));color:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-quiet-text-color-hover,var(--spectrum-alias-text-color-hover)))}.spectrum-ActionButton--quiet_e2d99e.focus-ring_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-quiet-background-color-key-focus,var(--spectrum-alias-background-color-transparent)));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-actionbutton-quiet-border-color-hover,var(--spectrum-alias-border-color-transparent)));color:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-quiet-text-color-key-focus,var(--spectrum-alias-text-color-hover)))}.spectrum-ActionButton--quiet_e2d99e.is-active_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-quiet-background-color-down,var(--spectrum-global-color-gray-300)));border-color:var(--spectrum-high-contrast-highlight,var(--spectrum-actionbutton-quiet-border-color-down,var(--spectrum-global-color-gray-300)));color:var(--spectrum-high-contrast-button-text,var(--spectrum-actionbutton-quiet-text-color-down,var(--spectrum-alias-text-color-down)))}.spectrum-ActionButton--quiet_e2d99e:disabled,.spectrum-ActionButton--quiet_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-high-contrast-button-face,var(--spectrum-actionbutton-quiet-background-color-disabled,var(--spectrum-alias-background-color-transparent)));border-color:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-quiet-border-color-disabled,var(--spectrum-alias-border-color-transparent)));color:var(--spectrum-high-contrast-gray-text,var(--spectrum-actionbutton-quiet-text-color-disabled,var(--spectrum-alias-text-color-disabled)))}.spectrum-ActionButton--staticWhite_e2d99e{mix-blend-mode:screen;--spectrum-actionbutton-static-background-color:var(--spectrum-actionbutton-static-white-background-color);--spectrum-actionbutton-static-background-color-hover:#ffffff1a;--spectrum-actionbutton-static-background-color-focus:#ffffff1a;--spectrum-actionbutton-static-background-color-active:#ffffff26;--spectrum-actionbutton-static-background-color-disabled:var(--spectrum-actionbutton-static-white-background-color-disabled);--spectrum-actionbutton-static-background-color-selected:#ffffffe6;--spectrum-actionbutton-static-background-color-selected-hover:white;--spectrum-actionbutton-static-background-color-selected-focus:white;--spectrum-actionbutton-static-background-color-selected-active:white;--spectrum-actionbutton-static-background-color-selected-disabled:#ffffff1a;--spectrum-actionbutton-static-border-color:var(--spectrum-actionbutton-static-white-border-color);--spectrum-actionbutton-static-border-color-hover:var(--spectrum-actionbutton-static-white-border-color-hover);--spectrum-actionbutton-static-border-color-active:var(--spectrum-actionbutton-static-white-border-color-down);--spectrum-actionbutton-static-border-color-focus:var(--spectrum-actionbutton-static-white-border-color-key-focus);--spectrum-actionbutton-static-border-disabled:var(--spectrum-actionbutton-static-white-border-color-disabled);--spectrum-actionbutton-static-border-color-selected-disabled:var(--spectrum-actionbutton-static-white-border-color-selected-disabled);--spectrum-actionbutton-static-color:white;--spectrum-actionbutton-static-color-selected:black;--spectrum-actionbutton-static-color-disabled:#ffffff8c}.spectrum-ActionButton--staticWhite_e2d99e.spectrum-ActionButton--quiet_e2d99e{--spectrum-actionbutton-static-border-color:transparent;--spectrum-actionbutton-static-border-color-hover:transparent;--spectrum-actionbutton-static-border-color-active:transparent}.spectrum-ActionButton--staticBlack_e2d99e{mix-blend-mode:multiply;--spectrum-actionbutton-static-background-color:var(--spectrum-actionbutton-static-black-background-color);--spectrum-actionbutton-static-background-color-hover:#0000001a;--spectrum-actionbutton-static-background-color-focus:#0000001a;--spectrum-actionbutton-static-background-color-active:#00000026;--spectrum-actionbutton-static-background-color-selected:#000000e6;--spectrum-actionbutton-static-background-color-disabled:var(--spectrum-actionbutton-static-black-background-color-disabled);--spectrum-actionbutton-static-background-color-selected-hover:black;--spectrum-actionbutton-static-background-color-selected-focus:black;--spectrum-actionbutton-static-background-color-selected-active:black;--spectrum-actionbutton-static-background-color-selected-disabled:#0000001a;--spectrum-actionbutton-static-border-color:var(--spectrum-actionbutton-static-black-border-color);--spectrum-actionbutton-static-border-color-hover:var(--spectrum-actionbutton-static-black-border-color-hover);--spectrum-actionbutton-static-border-color-active:var(--spectrum-actionbutton-static-black-border-color-down);--spectrum-actionbutton-static-border-color-focus:var(--spectrum-actionbutton-static-black-border-color-key-focus);--spectrum-actionbutton-static-border-disabled:var(--spectrum-actionbutton-static-black-border-color-disabled);--spectrum-actionbutton-static-border-color-selected-disabled:var(--spectrum-actionbutton-static-black-border-color-selected-disabled);--spectrum-actionbutton-static-color:black;--spectrum-actionbutton-static-color-selected:white;--spectrum-actionbutton-static-color-disabled:#0000008c}.spectrum-ActionButton--staticBlack_e2d99e.spectrum-ActionButton--quiet_e2d99e{--spectrum-actionbutton-static-border-color:transparent;--spectrum-actionbutton-static-border-color-hover:transparent;--spectrum-actionbutton-static-border-color-active:transparent}.spectrum-ActionButton--staticColor_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color);border-color:var(--spectrum-actionbutton-static-border-color);color:var(--spectrum-actionbutton-static-color);--spectrum-focus-ring-color:var(--spectrum-actionbutton-static-color)}.spectrum-ActionButton--staticColor_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-actionbutton-static-color)}.spectrum-ActionButton--staticColor_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color-hover);border-color:var(--spectrum-actionbutton-static-border-color-hover);color:var(--spectrum-actionbutton-static-color)}.spectrum-ActionButton--staticColor_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-hovered_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-actionbutton-static-color)}.spectrum-ActionButton--staticColor_e2d99e.focus-ring_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color-focus);border-color:var(--spectrum-actionbutton-static-border-color-focus);box-shadow:none;color:var(--spectrum-actionbutton-static-color)}.spectrum-ActionButton--staticColor_e2d99e.focus-ring_e2d99e.is-hovered_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.focus-ring_e2d99e.is-active_e2d99e{border-color:var(--spectrum-actionbutton-static-border-color-focus)}.spectrum-ActionButton--staticColor_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.focus-ring_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-actionbutton-static-color)}.spectrum-ActionButton--staticColor_e2d99e.is-active_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color-active);border-color:var(--spectrum-actionbutton-static-border-color-active);color:var(--spectrum-actionbutton-static-color)}.spectrum-ActionButton--staticColor_e2d99e.is-active_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-actionbutton-static-color)}.spectrum-ActionButton--staticColor_e2d99e:disabled,.spectrum-ActionButton--staticColor_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color-disabled);border-color:var(--spectrum-actionbutton-static-border-disabled);color:var(--spectrum-actionbutton-static-color-disabled)}.spectrum-ActionButton--staticColor_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e:disabled .spectrum-ActionButton-hold_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-disabled_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-actionbutton-static-color-disabled)}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color-selected);border-color:var(--spectrum-actionbutton-static-background-color-selected);color:var(--spectrum-actionbutton-static-color-selected)}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-actionbutton-static-color-selected)}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.focus-ring_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.focus-ring_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color-selected-focus);border-color:var(--spectrum-actionbutton-static-background-color-selected-focus);color:var(--spectrum-actionbutton-static-color-selected);box-shadow:none}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-actionbutton-static-color-selected)}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-hovered_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-active_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.is-hovered_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.is-active_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color-selected-hover);border-color:var(--spectrum-actionbutton-static-background-color-selected-hover);color:var(--spectrum-actionbutton-static-color-selected)}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-active_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.is-active_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-actionbutton-static-color-selected)}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e:disabled,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-disabled_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e:disabled,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-actionbutton-static-background-color-selected-disabled);border-color:var(--spectrum-actionbutton-static-border-color-selected-disabled);color:var(--spectrum-actionbutton-static-color-disabled)}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e:disabled .spectrum-ActionButton-hold_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e.is-disabled_e2d99e .spectrum-ActionButton-hold_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e:disabled .spectrum-ActionButton-hold_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.is-selected_e2d99e.is-disabled_e2d99e .spectrum-ActionButton-hold_e2d99e{fill:var(--spectrum-actionbutton-static-color-disabled)}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e{--spectrum-actionbutton-static-background-color:transparent;--spectrum-actionbutton-static-background-color-disabled:transparent}.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-selected_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-hovered_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-hovered_e2d99e.is-selected_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-active_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-active_e2d99e.is-selected_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e:disabled,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e:disabled.is-selected_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-disabled_e2d99e,.spectrum-ActionButton--staticColor_e2d99e.spectrum-ActionButton--quiet_e2d99e.is-disabled_e2d99e.is-selected_e2d99e{border-color:#0000}.spectrum-LogicButton--and_e2d99e{background-color:var(--spectrum-global-color-static-blue-600,#1473e6);border-color:var(--spectrum-global-color-static-blue-600,#1473e6);color:var(--spectrum-logicbutton-and-text-color,var(--spectrum-global-color-static-white))}.spectrum-LogicButton--and_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-global-color-static-blue-700,#0d66d0);border-color:var(--spectrum-global-color-static-blue-700,#0d66d0);color:var(--spectrum-logicbutton-and-text-color,var(--spectrum-global-color-static-white))}.spectrum-LogicButton--and_e2d99e:disabled,.spectrum-LogicButton--and_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-logicbutton-and-background-color-disabled,var(--spectrum-global-color-gray-200));border-color:var(--spectrum-logicbutton-and-border-color-disabled,var(--spectrum-global-color-gray-200));color:var(--spectrum-logicbutton-and-text-color-disabled,var(--spectrum-alias-text-color-disabled))}.spectrum-LogicButton--or_e2d99e{background-color:var(--spectrum-global-color-static-magenta-500,#d83790);border-color:var(--spectrum-global-color-static-magenta-500,#d83790);color:var(--spectrum-logicbutton-or-text-color,var(--spectrum-global-color-static-white))}.spectrum-LogicButton--or_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-global-color-static-magenta-600,#ca2982);border-color:var(--spectrum-global-color-static-magenta-600,#ca2982);color:var(--spectrum-logicbutton-or-text-color,var(--spectrum-global-color-static-white))}.spectrum-LogicButton--or_e2d99e:disabled,.spectrum-LogicButton--or_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-button-secondary-background-color-disabled,var(--spectrum-global-color-gray-200));border-color:var(--spectrum-button-secondary-border-color-disabled,var(--spectrum-global-color-gray-200));color:var(--spectrum-logicbutton-and-text-color-disabled,var(--spectrum-alias-text-color-disabled))}.spectrum-FieldButton_e2d99e{color:var(--spectrum-fieldbutton-text-color,var(--spectrum-alias-text-color));background-color:var(--spectrum-fieldbutton-background-color,var(--spectrum-global-color-gray-75));border-color:var(--spectrum-fieldbutton-border-color,var(--spectrum-alias-border-color))}.spectrum-FieldButton_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-fieldbutton-icon-color,var(--spectrum-alias-icon-color))}.spectrum-FieldButton_e2d99e.is-hovered_e2d99e{color:var(--spectrum-fieldbutton-text-color-hover,var(--spectrum-alias-text-color-hover));background-color:var(--spectrum-fieldbutton-background-color-hover,var(--spectrum-global-color-gray-50));border-color:var(--spectrum-fieldbutton-border-color-hover,var(--spectrum-alias-border-color-hover))}.spectrum-FieldButton_e2d99e.is-hovered_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-fieldbutton-icon-color-hover,var(--spectrum-alias-icon-color-hover))}.spectrum-FieldButton_e2d99e.is-active_e2d99e,.spectrum-FieldButton_e2d99e.is-selected_e2d99e{background-color:var(--spectrum-fieldbutton-background-color-down,var(--spectrum-global-color-gray-200));border-color:var(--spectrum-fieldbutton-border-color-down,var(--spectrum-alias-border-color-down))}.spectrum-FieldButton_e2d99e.is-active_e2d99e .spectrum-Icon_e2d99e,.spectrum-FieldButton_e2d99e.is-selected_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-fieldbutton-icon-color-down,var(--spectrum-alias-icon-color-down))}.spectrum-FieldButton_e2d99e.focus-ring_e2d99e,.spectrum-FieldButton_e2d99e.is-focused_e2d99e{background-color:var(--spectrum-fieldbutton-background-color-key-focus,var(--spectrum-global-color-gray-50));border-color:var(--spectrum-fieldbutton-border-color-key-focus,var(--spectrum-alias-border-color-focus));color:var(--spectrum-fieldbutton-text-color-key-focus,var(--spectrum-alias-text-color-hover))}.spectrum-FieldButton_e2d99e.focus-ring_e2d99e .spectrum-Icon_e2d99e,.spectrum-FieldButton_e2d99e.is-focused_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-fieldbutton-icon-color-key-focus,var(--spectrum-alias-icon-color-focus))}.spectrum-FieldButton_e2d99e.focus-ring_e2d99e.is-placeholder_e2d99e,.spectrum-FieldButton_e2d99e.is-focused_e2d99e.is-placeholder_e2d99e{fill:var(--spectrum-fieldbutton-placeholder-text-color-key-focus,var(--spectrum-alias-placeholder-text-color-hover))}.spectrum-FieldButton_e2d99e.spectrum-FieldButton--invalid_e2d99e{border-color:var(--spectrum-fieldbutton-border-color-error,var(--spectrum-global-color-red-500))}.spectrum-FieldButton_e2d99e.spectrum-FieldButton--invalid_e2d99e.is-hovered_e2d99e{border-color:var(--spectrum-fieldbutton-border-color-error-hover,var(--spectrum-global-color-red-600))}.spectrum-FieldButton_e2d99e.spectrum-FieldButton--invalid_e2d99e.is-active_e2d99e,.spectrum-FieldButton_e2d99e.spectrum-FieldButton--invalid_e2d99e.is-selected_e2d99e{border-color:var(--spectrum-fieldbutton-border-color-error-down,var(--spectrum-global-color-red-600))}.spectrum-FieldButton_e2d99e.spectrum-FieldButton--invalid_e2d99e.focus-ring_e2d99e,.spectrum-FieldButton_e2d99e.spectrum-FieldButton--invalid_e2d99e.is-focused_e2d99e{border-color:var(--spectrum-fieldbutton-border-color-error-key-focus,var(--spectrum-alias-border-color-focus))}.spectrum-FieldButton_e2d99e:disabled,.spectrum-FieldButton_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-fieldbutton-background-color-disabled,var(--spectrum-global-color-gray-200));color:var(--spectrum-fieldbutton-text-color-disabled,var(--spectrum-alias-text-color-disabled))}.spectrum-FieldButton_e2d99e:disabled .spectrum-Icon_e2d99e,.spectrum-FieldButton_e2d99e.is-disabled_e2d99e .spectrum-Icon_e2d99e{fill:var(--spectrum-fieldbutton-icon-color-disabled,var(--spectrum-alias-icon-color-disabled))}.spectrum-FieldButton--quiet_e2d99e{color:var(--spectrum-fieldbutton-text-color,var(--spectrum-alias-text-color));border-color:var(--spectrum-fieldbutton-quiet-border-color,var(--spectrum-alias-border-color-transparent));background-color:var(--spectrum-fieldbutton-quiet-background-color,var(--spectrum-alias-background-color-transparent))}.spectrum-FieldButton--quiet_e2d99e.is-hovered_e2d99e{background-color:var(--spectrum-fieldbutton-quiet-background-color-hover,var(--spectrum-alias-background-color-transparent));color:var(--spectrum-fieldbutton-text-color-hover,var(--spectrum-alias-text-color-hover))}.spectrum-FieldButton--quiet_e2d99e.focus-ring_e2d99e,.spectrum-FieldButton--quiet_e2d99e.is-focused_e2d99e{background-color:var(--spectrum-fieldbutton-quiet-background-color-key-focus,var(--spectrum-alias-background-color-transparent))}.spectrum-FieldButton--quiet_e2d99e.focus-ring_e2d99e.is-placeholder_e2d99e,.spectrum-FieldButton--quiet_e2d99e.is-focused_e2d99e.is-placeholder_e2d99e{color:var(--spectrum-fieldbutton-quiet-placeholder-text-color-key-focus,var(--spectrum-alias-placeholder-text-color-hover))}.spectrum-FieldButton--quiet_e2d99e.is-active_e2d99e,.spectrum-FieldButton--quiet_e2d99e.is-selected_e2d99e{background-color:var(--spectrum-fieldbutton-quiet-background-color-down,var(--spectrum-alias-background-color-transparent));border-color:var(--spectrum-fieldbutton-quiet-border-color-down,var(--spectrum-alias-border-color-transparent))}.spectrum-FieldButton--quiet_e2d99e.is-active_e2d99e.focus-ring_e2d99e,.spectrum-FieldButton--quiet_e2d99e.is-active_e2d99e.is-focused_e2d99e,.spectrum-FieldButton--quiet_e2d99e.is-selected_e2d99e.focus-ring_e2d99e,.spectrum-FieldButton--quiet_e2d99e.is-selected_e2d99e.is-focused_e2d99e{background-color:var(--spectrum-fieldbutton-quiet-background-color-key-focus,var(--spectrum-alias-background-color-transparent))}.spectrum-FieldButton--quiet_e2d99e:disabled,.spectrum-FieldButton--quiet_e2d99e.is-disabled_e2d99e{background-color:var(--spectrum-fieldbutton-quiet-background-color-disabled,var(--spectrum-alias-background-color-transparent));color:var(--spectrum-fieldbutton-text-color-disabled,var(--spectrum-alias-text-color-disabled))}@media (forced-colors:active){.spectrum-ActionButton_e2d99e,.spectrum-ClearButton_e2d99e,.spectrum-LogicButton_e2d99e,.spectrum-FieldButton_e2d99e{forced-color-adjust:none;--spectrum-clearbutton-medium-background-color:ButtonFace;--spectrum-clearbutton-medium-background-color-disabled:ButtonFace;--spectrum-clearbutton-medium-background-color-down:ButtonFace;--spectrum-clearbutton-medium-background-color-hover:ButtonFace;--spectrum-clearbutton-medium-background-color-key-focus:ButtonFace;--spectrum-clearbutton-medium-icon-color:ButtonText;--spectrum-clearbutton-medium-icon-color-disabled:GrayText;--spectrum-clearbutton-medium-icon-color-down:Highlight;--spectrum-clearbutton-medium-icon-color-hover:Highlight;--spectrum-clearbutton-medium-icon-color-key-focus:Highlight;--spectrum-fieldbutton-background-color:ButtonFace;--spectrum-fieldbutton-background-color-disabled:ButtonFace;--spectrum-fieldbutton-background-color-down:ButtonFace;--spectrum-fieldbutton-background-color-hover:ButtonFace;--spectrum-fieldbutton-background-color-key-focus:ButtonFace;--spectrum-fieldbutton-border-color:ButtonText;--spectrum-fieldbutton-border-color-down:Highlight;--spectrum-fieldbutton-border-color-error:ButtonText;--spectrum-fieldbutton-border-color-error-down:Highlight;--spectrum-fieldbutton-border-color-error-hover:Highlight;--spectrum-fieldbutton-border-color-error-key-focus:Highlight;--spectrum-fieldbutton-border-color-hover:Highlight;--spectrum-fieldbutton-border-color-key-focus:Highlight;--spectrum-fieldbutton-icon-color-disabled:GrayText;--spectrum-fieldbutton-placeholder-text-color-key-focus:ButtonText;--spectrum-fieldbutton-quiet-background-color:ButtonFace;--spectrum-fieldbutton-quiet-background-color-disabled:ButtonFace;--spectrum-fieldbutton-quiet-background-color-down:ButtonFace;--spectrum-fieldbutton-quiet-background-color-hover:ButtonFace;--spectrum-fieldbutton-quiet-background-color-key-focus:ButtonFace;--spectrum-fieldbutton-quiet-border-color:ButtonFace;--spectrum-fieldbutton-quiet-border-color-down:Highlight;--spectrum-fieldbutton-quiet-placeholder-text-color-key-focus:ButtonText;--spectrum-fieldbutton-text-color:ButtonText;--spectrum-fieldbutton-text-color-disabled:GrayText;--spectrum-fieldbutton-text-color-hover:ButtonText;--spectrum-fieldbutton-text-color-key-focus:ButtonText;--spectrum-logicbutton-and-background-color:ButtonFace;--spectrum-logicbutton-and-background-color-disabled:ButtonFace;--spectrum-logicbutton-and-background-color-hover:ButtonFace;--spectrum-logicbutton-and-border-color:ButtonText;--spectrum-logicbutton-and-border-color-disabled:GrayText;--spectrum-logicbutton-and-border-color-hover:Highlight;--spectrum-logicbutton-and-text-color:ButtonText;--spectrum-logicbutton-and-text-color-disabled:GrayText;--spectrum-logicbutton-or-background-color:ButtonFace;--spectrum-logicbutton-or-background-color-hover:ButtonFace;--spectrum-logicbutton-or-border-color:ButtonText;--spectrum-logicbutton-or-border-color-hover:Highlight;--spectrum-logicbutton-or-text-color:ButtonText;--spectrum-button-primary-focus-ring-color-key-focus:CanvasText;--spectrum-button-primary-focus-ring-size-key-focus:3px;--spectrum-dropdown-border-color-key-focus:Highlight}.spectrum-Button--overBackground_e2d99e{--spectrum-button-over-background-color:ButtonText}.spectrum-ActionButton--staticWhite_e2d99e,.spectrum-ActionButton--staticBlack_e2d99e{mix-blend-mode:normal;--spectrum-actionbutton-static-background-color-hover:ButtonFace;--spectrum-actionbutton-static-background-color-focus:ButtonFace;--spectrum-actionbutton-static-background-color-active:ButtonFace;--spectrum-actionbutton-static-background-color-selected:Highlight;--spectrum-actionbutton-static-background-color-selected-hover:Highlight;--spectrum-actionbutton-static-background-color-selected-focus:Highlight;--spectrum-actionbutton-static-background-color-selected-active:Highlight;--spectrum-actionbutton-static-border-color:ButtonText;--spectrum-actionbutton-static-border-color-hover:ButtonText;--spectrum-actionbutton-static-border-color-active:ButtonText;--spectrum-actionbutton-static-border-color-focus:CanvasText;--spectrum-actionbutton-static-border-disabled:GrayText;--spectrum-actionbutton-static-color:ButtonText;--spectrum-actionbutton-static-color-selected:HighlightText;--spectrum-actionbutton-static-color-disabled:GrayText}.spectrum-FieldButton_e2d99e.focus-ring_e2d99e:not(.spectrum-FieldButton--quiet_e2d99e),.spectrum-FieldButton_e2d99e.is-focused_e2d99e:not(.spectrum-FieldButton--quiet_e2d99e){outline:2px solid Highlight}.spectrum-FieldButton_e2d99e.focus-ring_e2d99e.spectrum-FieldButton--quiet_e2d99e,.spectrum-FieldButton_e2d99e.is-focused_e2d99e.spectrum-FieldButton--quiet_e2d99e{forced-color-adjust:none;box-shadow:0 2px 0 0 var(--spectrum-dropdown-border-color-key-focus,var(--spectrum-alias-border-color-focus))}}', {});
	const Xe = {
			prefix: String(Math.round(1e10 * Math.random())),
			current: 0
		},
		Ye = h.createContext(Xe);
	let Je = Boolean("undefined" != typeof window && window.document && window.document.createElement);

	function Ze(e) {
		var t, r, n = "";
		if ("string" == typeof e || "number" == typeof e) n += e;
		else if ("object" == typeof e)
			if (Array.isArray(e))
				for (t = 0; t < e.length; t++) e[t] && (r = Ze(e[t])) && (n && (n += " "), n += r);
			else
				for (t in e) e[t] && (n && (n += " "), n += t);
		return n
	}

	function et() {
		for (var e, t, r = 0, n = ""; r < arguments.length;)(e = arguments[r++]) && (t = Ze(e)) && (n && (n += " "), n += t);
		return n
	}
	const tt = "undefined" != typeof window ? h.useLayoutEffect : () => {};
	let rt = new Map;

	function nt(e) {
		let [t, r] = u.useState(e), n = u.useRef(null), o = function(e) {
			let t = u.useContext(Ye);
			return t !== Xe || Je || console.warn("When server rendering, you must wrap your application in an <SSRProvider> to ensure consistent ids are generated between the client and server."), u.useMemo((() => e || `react-aria${t.prefix}-${++t.current}`), [e])
		}(t), i = u.useCallback((e => {
			n.current = e
		}), []);
		return rt.set(o, i), tt((() => {
			let e = o;
			return () => {
				rt.delete(e)
			}
		}), [o]), u.useEffect((() => {
			let e = n.current;
			e && (n.current = null, r(e))
		})), o
	}

	function ot(e, t) {
		if (e === t) return e;
		let r = rt.get(e);
		if (r) return r(t), t;
		let n = rt.get(t);
		return n ? (n(e), e) : t
	}

	function it(e = []) {
		let t = nt(),
			[r, n] = function(e) {
				let [t, r] = u.useState(e), n = u.useRef(t), o = u.useRef(null);
				n.current = t;
				let i = u.useRef(null);
				i.current = () => {
					let e = o.current.next();
					e.done ? o.current = null : t === e.value ? i.current() : r(e.value)
				}, tt((() => {
					o.current && i.current()
				}));
				let a = u.useCallback((e => {
					o.current = e(n.current), i.current()
				}), [o, i]);
				return [t, a]
			}(t),
			o = u.useCallback((() => {
				n((function*() {
					yield t, yield document.getElementById(t) ? t : void 0
				}))
			}), [t, n]);
		return tt(o, [t, o, ...e]), r
	}

	function at(...e) {
		return (...t) => {
			for (let r of e) "function" == typeof r && r(...t)
		}
	}

	function ct(...e) {
		let t = {
			...e[0]
		};
		for (let r = 1; r < e.length; r++) {
			let n = e[r];
			for (let e in n) {
				let r = t[e],
					o = n[e];
				"function" == typeof r && "function" == typeof o && "o" === e[0] && "n" === e[1] && e.charCodeAt(2) >= 65 && e.charCodeAt(2) <= 90 ? t[e] = at(r, o) : "className" !== e && "UNSAFE_className" !== e || "string" != typeof r || "string" != typeof o ? "id" === e && r && o ? t.id = ot(r, o) : t[e] = void 0 !== o ? o : r : t[e] = et(r, o)
			}
		}
		return t
	}
	const st = new Set(["id"]),
		ut = new Set(["aria-label", "aria-labelledby", "aria-describedby", "aria-details"]),
		lt = /^(data-.*)$/;

	function dt(e, t = {}) {
		let {
			labelable: r,
			propNames: n
		} = t, o = {};
		for (const t in e) Object.prototype.hasOwnProperty.call(e, t) && (st.has(t) || r && ut.has(t) || (null == n ? void 0 : n.has(t)) || lt.test(t)) && (o[t] = e[t]);
		return o
	}

	function pt(e) {
		if (function() {
				if (null == ft) {
					ft = !1;
					try {
						document.createElement("div").focus({
							get preventScroll() {
								return ft = !0, !0
							}
						})
					} catch (e) {}
				}
				return ft
			}()) e.focus({
			preventScroll: !0
		});
		else {
			let t = function(e) {
				var t = e.parentNode,
					r = [],
					n = document.scrollingElement || document.documentElement;
				for (; t instanceof HTMLElement && t !== n;)(t.offsetHeight < t.scrollHeight || t.offsetWidth < t.scrollWidth) && r.push({
					element: t,
					scrollTop: t.scrollTop,
					scrollLeft: t.scrollLeft
				}), t = t.parentNode;
				n instanceof HTMLElement && r.push({
					element: n,
					scrollTop: n.scrollTop,
					scrollLeft: n.scrollLeft
				});
				return r
			}(e);
			e.focus(),
				function(e) {
					for (let {
							element: t,
							scrollTop: r,
							scrollLeft: n
						} of e) t.scrollTop = r, t.scrollLeft = n
				}(t)
		}
	}
	let ft = null;
	let mt = new Map,
		gt = new Set;

	function ht() {
		if ("undefined" == typeof window) return;
		let e = t => {
			let r = mt.get(t.target);
			if (r && (r.delete(t.propertyName), 0 === r.size && (t.target.removeEventListener("transitioncancel", e), mt.delete(t.target)), 0 === mt.size)) {
				for (let e of gt) e();
				gt.clear()
			}
		};
		document.body.addEventListener("transitionrun", (t => {
			let r = mt.get(t.target);
			r || (r = new Set, mt.set(t.target, r), t.target.addEventListener("transitioncancel", e)), r.add(t.propertyName)
		})), document.body.addEventListener("transitionend", e)
	}

	function vt(e) {
		requestAnimationFrame((() => {
			0 === mt.size ? e() : gt.add(e)
		}))
	}

	function bt(e, t) {
		tt((() => {
			if (e && e.ref && t) return e.ref.current = t.current, () => {
				e.ref.current = null
			}
		}), [e, t])
	}

	function yt(e) {
		var t;
		return "undefined" != typeof window && null != window.navigator && e.test((null === (t = window.navigator.userAgentData) || void 0 === t ? void 0 : t.platform) || window.navigator.platform)
	}

	function _t() {
		return yt(/^Mac/i)
	}

	function wt() {
		return yt(/^iPhone/i) || yt(/^iPad/i) || _t() && navigator.maxTouchPoints > 1
	}

	function kt() {
		return e = /Android/i, "undefined" != typeof window && null != window.navigator && ((null === (t = window.navigator.userAgentData) || void 0 === t ? void 0 : t.brands.some((t => e.test(t.brand)))) || e.test(window.navigator.userAgent));
		var e, t
	}

	function xt(e) {
		return !(0 !== e.mozInputSource || !e.isTrusted) || (kt() && e.pointerType ? "click" === e.type && 1 === e.buttons : 0 === e.detail && !e.pointerType)
	}
	"undefined" != typeof document && ("loading" !== document.readyState ? ht() : document.addEventListener("DOMContentLoaded", ht));
	class Tt {
		getStringForLocale(e, t) {
			let r = this.strings[t];
			r || (r = function(e, t, r = "en-US") {
				if (t[e]) return t[e];
				let n = function(e) {
					return Intl.Locale ? new Intl.Locale(e).language : e.split("-")[0]
				}(e);
				if (t[n]) return t[n];
				for (let e in t)
					if (e.startsWith(n + "-")) return t[e];
				return t[r]
			}(t, this.strings, this.defaultLocale), this.strings[t] = r);
			let n = r[e];
			if (!n) throw new Error(`Could not find intl message ${e} in ${t} locale`);
			return n
		}
		constructor(e, t = "en-US") {
			this.strings = {
				...e
			}, this.defaultLocale = t
		}
	}
	const St = new Map,
		It = new Map;
	class Et {
		format(e, t) {
			let r = this.strings.getStringForLocale(e, this.locale);
			return "function" == typeof r ? r(t, this) : r
		}
		plural(e, t, r = "cardinal") {
			let n = t["=" + e];
			if (n) return "function" == typeof n ? n() : n;
			let o = this.locale + ":" + r,
				i = St.get(o);
			return i || (i = new Intl.PluralRules(this.locale, {
				type: r
			}), St.set(o, i)), n = t[i.select(e)] || t.other, "function" == typeof n ? n() : n
		}
		number(e) {
			let t = It.get(this.locale);
			return t || (t = new Intl.NumberFormat(this.locale), It.set(this.locale, t)), t.format(e)
		}
		select(e, t) {
			let r = e[t] || e.other;
			return "function" == typeof r ? r() : r
		}
		constructor(e, t) {
			this.locale = e, this.strings = t
		}
	}
	const Pt = new Set(["Arab", "Syrc", "Samr", "Mand", "Thaa", "Mend", "Nkoo", "Adlm", "Rohg", "Hebr"]),
		Dt = new Set(["ae", "ar", "arc", "bcc", "bqi", "ckb", "dv", "fa", "glk", "he", "ku", "mzn", "nqo", "pnb", "ps", "sd", "ug", "ur", "yi"]);

	function Ct(e) {
		if (Intl.Locale) {
			let t = new Intl.Locale(e).maximize().script;
			return Pt.has(t)
		}
		let t = e.split("-")[0];
		return Dt.has(t)
	}

	function At() {
		let e = "undefined" != typeof navigator && (navigator.language || navigator.userLanguage) || "en-US";
		try {
			Intl.DateTimeFormat.supportedLocalesOf([e])
		} catch (t) {
			e = "en-US"
		}
		return {
			locale: e,
			direction: Ct(e) ? "rtl" : "ltr"
		}
	}
	let Ot = At(),
		Bt = new Set;

	function Rt() {
		Ot = At();
		for (let e of Bt) e(Ot)
	}

	function Mt() {
		let e = function() {
				let e = u.useContext(Ye) !== Xe,
					[t, r] = u.useState(e);
				return "undefined" != typeof window && e && u.useLayoutEffect((() => {
					r(!1)
				}), []), t
			}(),
			[t, r] = u.useState(Ot);
		return u.useEffect((() => (0 === Bt.size && window.addEventListener("languagechange", Rt), Bt.add(r), () => {
			Bt.delete(r), 0 === Bt.size && window.removeEventListener("languagechange", Rt)
		})), []), e ? {
			locale: "en-US",
			direction: "ltr"
		} : t
	}
	const zt = h.createContext(null);

	function Ft() {
		let e = Mt();
		return u.useContext(zt) || e
	}
	const jt = new WeakMap;

	function Lt(e) {
		let {
			locale: t
		} = Ft(), r = u.useMemo((() => function(e) {
			let t = jt.get(e);
			return t || (t = new Tt(e), jt.set(e, t)), t
		}(e)), [e]);
		return u.useMemo((() => new Et(t, r)), [t, r])
	}
	var Ut = function() {
		return Ut = Object.assign || function(e) {
			for (var t, r = 1, n = arguments.length; r < n; r++)
				for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
			return e
		}, Ut.apply(this, arguments)
	};

	function Nt(e, t) {
		var r = {};
		for (var n in e) Object.prototype.hasOwnProperty.call(e, n) && t.indexOf(n) < 0 && (r[n] = e[n]);
		if (null != e && "function" == typeof Object.getOwnPropertySymbols) {
			var o = 0;
			for (n = Object.getOwnPropertySymbols(e); o < n.length; o++) t.indexOf(n[o]) < 0 && Object.prototype.propertyIsEnumerable.call(e, n[o]) && (r[n[o]] = e[n[o]])
		}
		return r
	}

	function Ht(e, ...t) {
		let r = [];
		for (let n of t)
			if ("object" == typeof n && n) {
				let t = {};
				for (let r in n) e[r] && (t[e[r]] = n[r]), e[r] || (t[r] = n[r]);
				r.push(t)
			} else "string" == typeof n ? (e[n] && r.push(e[n]), e[n] || r.push(n)) : r.push(n);
		return et(...r)
	}

	function Vt(e) {
		return {
			UNSAFE_getDOMNode: () => e.current
		}
	}

	function qt(e) {
		let t = u.useRef(null);
		return u.useImperativeHandle(e, (() => Vt(t))), t
	}

	function Wt(e, t) {
		let r = u.useRef(null);
		return u.useImperativeHandle(e, (() => function(e, t = e) {
			return {
				...Vt(e),
				focus() {
					t.current && t.current.focus()
				}
			}
		}(r, t))), r
	}

	function Gt(e) {
		return {
			get current() {
				return e.current && e.current.UNSAFE_getDOMNode()
			}
		}
	}
	const Kt = h.createContext(null);
	Kt.displayName = "BreakpointContext";
	const $t = {
		margin: ["margin", er],
		marginStart: [Xt("marginLeft", "marginRight"), er],
		marginEnd: [Xt("marginRight", "marginLeft"), er],
		marginTop: ["marginTop", er],
		marginBottom: ["marginBottom", er],
		marginX: [
			["marginLeft", "marginRight"], er
		],
		marginY: [
			["marginTop", "marginBottom"], er
		],
		width: ["width", er],
		height: ["height", er],
		minWidth: ["minWidth", er],
		minHeight: ["minHeight", er],
		maxWidth: ["maxWidth", er],
		maxHeight: ["maxHeight", er],
		isHidden: ["display", function(e) {
			return e ? "none" : void 0
		}],
		alignSelf: ["alignSelf", nr],
		justifySelf: ["justifySelf", nr],
		position: ["position", tr],
		zIndex: ["zIndex", tr],
		top: ["top", er],
		bottom: ["bottom", er],
		start: [Xt("left", "right"), er],
		end: [Xt("right", "left"), er],
		left: ["left", er],
		right: ["right", er],
		order: ["order", tr],
		flex: ["flex", function(e) {
			return "boolean" == typeof e ? e ? "1" : void 0 : "" + e
		}],
		flexGrow: ["flexGrow", nr],
		flexShrink: ["flexShrink", nr],
		flexBasis: ["flexBasis", nr],
		gridArea: ["gridArea", nr],
		gridColumn: ["gridColumn", nr],
		gridColumnEnd: ["gridColumnEnd", nr],
		gridColumnStart: ["gridColumnStart", nr],
		gridRow: ["gridRow", nr],
		gridRowEnd: ["gridRowEnd", nr],
		gridRowStart: ["gridRowStart", nr]
	};
	Xt("borderLeftWidth", "borderRightWidth"), Xt("borderRightWidth", "borderLeftWidth"), Xt("borderLeftColor", "borderRightColor"), Xt("borderRightColor", "borderLeftColor"), Xt("borderTopLeftRadius", "borderTopRightRadius"), Xt("borderTopRightRadius", "borderTopLeftRadius"), Xt("borderBottomLeftRadius", "borderBottomRightRadius"), Xt("borderBottomRightRadius", "borderBottomLeftRadius"), Xt("paddingLeft", "paddingRight"), Xt("paddingRight", "paddingLeft");
	const Qt = {
		borderWidth: "borderStyle",
		borderLeftWidth: "borderLeftStyle",
		borderRightWidth: "borderRightStyle",
		borderTopWidth: "borderTopStyle",
		borderBottomWidth: "borderBottomStyle"
	};

	function Xt(e, t) {
		return r => "rtl" === r ? t : e
	}
	const Yt = /(%|px|em|rem|vw|vh|auto|cm|mm|in|pt|pc|ex|ch|rem|vmin|vmax|fr)$/,
		Jt = /^\s*\w+\(/,
		Zt = /(static-)?size-\d+|single-line-(height|width)/g;

	function er(e) {
		return "number" == typeof e ? e + "px" : Yt.test(e) ? e : Jt.test(e) ? e.replace(Zt, "var(--spectrum-global-dimension-$&, var(--spectrum-alias-$&))") : `var(--spectrum-global-dimension-${e}, var(--spectrum-alias-${e}))`
	}

	function tr(e) {
		return e
	}

	function rr(e, t = $t, r = {}) {
		let {
			UNSAFE_className: n,
			UNSAFE_style: o,
			...i
		} = e, a = u.useContext(Kt), {
			direction: c
		} = Ft(), {
			matchedBreakpoints: s = (null == a ? void 0 : a.matchedBreakpoints) || ["base"]
		} = r, l = function(e, t, r, n) {
			let o = {};
			for (let i in e) {
				let a = t[i];
				if (!a || null == e[i]) continue;
				let [c, s] = a;
				"function" == typeof c && (c = c(r));
				let u = s(or(e[i], n), e.colorVersion);
				if (Array.isArray(c))
					for (let e of c) o[e] = u;
				else o[c] = u
			}
			for (let e in Qt) o[e] && (o[Qt[e]] = "solid", o.boxSizing = "border-box");
			return o
		}(e, t, c, s), d = {
			...o,
			...l
		};
		i.className && console.warn("The className prop is unsafe and is unsupported in React Spectrum v3. Please use style props with Spectrum variables, or UNSAFE_className if you absolutely must do something custom. Note that this may break in future versions due to DOM structure changes."), i.style && console.warn("The style prop is unsafe and is unsupported in React Spectrum v3. Please use style props with Spectrum variables, or UNSAFE_style if you absolutely must do something custom. Note that this may break in future versions due to DOM structure changes.");
		let p = {
			style: d,
			className: n
		};
		return or(e.isHidden, s) && (p.hidden = !0), {
			styleProps: p
		}
	}

	function nr(e) {
		return e
	}

	function or(e, t) {
		if (e && "object" == typeof e && !Array.isArray(e)) {
			for (let r = 0; r < t.length; r++) {
				let n = t[r];
				if (null != e[n]) return e[n]
			}
			return e.base
		}
		return e
	}
	let ir = h.createContext(null);

	function ar(e, t) {
		let r = e.slot || t,
			{
				[r]: n = {}
			} = u.useContext(ir) || {};
		return ct(e, ct(n, {
			id: e.id
		}))
	}

	function cr(e) {
		let t = u.useContext(ir) || {},
			{
				slots: r = {},
				children: n
			} = e,
			o = u.useMemo((() => Object.keys(t).concat(Object.keys(r)).reduce(((e, n) => ({
				...e,
				[n]: ct(t[n] || {}, r[n] || {})
			})), {})), [t, r]);
		return h.createElement(ir.Provider, {
			value: o
		}, n)
	}

	function sr(e, t) {
		let [r, n] = u.useState(!0);
		return tt((() => {
			n(!(!t.current || !t.current.querySelector(e)))
		}), [n, e, t]), r
	}
	let ur = "default",
		lr = "",
		dr = new WeakMap;

	function pr(e) {
		wt() ? ("default" === ur && (lr = document.documentElement.style.webkitUserSelect, document.documentElement.style.webkitUserSelect = "none"), ur = "disabled") : (e instanceof HTMLElement || e instanceof SVGElement) && (dr.set(e, e.style.userSelect), e.style.userSelect = "none")
	}

	function fr(e) {
		if (wt()) {
			if ("disabled" !== ur) return;
			ur = "restoring", setTimeout((() => {
				vt((() => {
					"restoring" === ur && ("none" === document.documentElement.style.webkitUserSelect && (document.documentElement.style.webkitUserSelect = lr || ""), lr = "", ur = "default")
				}))
			}), 300)
		} else if ((e instanceof HTMLElement || e instanceof SVGElement) && e && dr.has(e)) {
			let t = dr.get(e);
			"none" === e.style.userSelect && (e.style.userSelect = t), "" === e.getAttribute("style") && e.removeAttribute("style"), dr.delete(e)
		}
	}
	const mr = h.createContext(null);

	function gr(e) {
		let {
			onPress: t,
			onPressChange: r,
			onPressStart: n,
			onPressEnd: o,
			onPressUp: i,
			isDisabled: a,
			isPressed: c,
			preventFocusOnPress: s,
			shouldCancelOnPointerExit: l,
			allowTextSelectionOnPress: d,
			ref: p,
			...f
		} = function(e) {
			let t = u.useContext(mr);
			if (t) {
				let {
					register: r,
					...n
				} = t;
				e = ct(n, e), r()
			}
			return bt(t, e.ref), e
		}(e), m = u.useRef(null);
		m.current = {
			onPress: t,
			onPressChange: r,
			onPressStart: n,
			onPressEnd: o,
			onPressUp: i,
			isDisabled: a,
			shouldCancelOnPointerExit: l
		};
		let [g, h] = u.useState(!1), v = u.useRef({
			isPressed: !1,
			ignoreEmulatedMouseEvents: !1,
			ignoreClickAfterPress: !1,
			didFirePressStart: !1,
			activePointerId: null,
			target: null,
			isOverTarget: !1,
			pointerType: null
		}), {
			addGlobalListener: b,
			removeAllGlobalListeners: y
		} = function() {
			let e = u.useRef(new Map),
				t = u.useCallback(((t, r, n, o) => {
					let i = (null == o ? void 0 : o.once) ? (...t) => {
						e.current.delete(n), n(...t)
					} : n;
					e.current.set(n, {
						type: r,
						eventTarget: t,
						fn: i,
						options: o
					}), t.addEventListener(r, n, o)
				}), []),
				r = u.useCallback(((t, r, n, o) => {
					var i;
					let a = (null === (i = e.current.get(n)) || void 0 === i ? void 0 : i.fn) || n;
					t.removeEventListener(r, a, o), e.current.delete(n)
				}), []),
				n = u.useCallback((() => {
					e.current.forEach(((e, t) => {
						r(e.eventTarget, e.type, t, e.options)
					}))
				}), [r]);
			return u.useEffect((() => n), [n]), {
				addGlobalListener: t,
				removeGlobalListener: r,
				removeAllGlobalListeners: n
			}
		}(), _ = u.useMemo((() => {
			let e = v.current,
				t = (t, r) => {
					let {
						onPressStart: n,
						onPressChange: o,
						isDisabled: i
					} = m.current;
					i || e.didFirePressStart || (n && n({
						type: "pressstart",
						pointerType: r,
						target: t.currentTarget,
						shiftKey: t.shiftKey,
						metaKey: t.metaKey,
						ctrlKey: t.ctrlKey,
						altKey: t.altKey
					}), o && o(!0), e.didFirePressStart = !0, h(!0))
				},
				r = (t, r, n = !0) => {
					let {
						onPressEnd: o,
						onPressChange: i,
						onPress: a,
						isDisabled: c
					} = m.current;
					e.didFirePressStart && (e.ignoreClickAfterPress = !0, e.didFirePressStart = !1, o && o({
						type: "pressend",
						pointerType: r,
						target: t.currentTarget,
						shiftKey: t.shiftKey,
						metaKey: t.metaKey,
						ctrlKey: t.ctrlKey,
						altKey: t.altKey
					}), i && i(!1), h(!1), a && n && !c && a({
						type: "press",
						pointerType: r,
						target: t.currentTarget,
						shiftKey: t.shiftKey,
						metaKey: t.metaKey,
						ctrlKey: t.ctrlKey,
						altKey: t.altKey
					}))
				},
				n = (e, t) => {
					let {
						onPressUp: r,
						isDisabled: n
					} = m.current;
					n || r && r({
						type: "pressup",
						pointerType: t,
						target: e.currentTarget,
						shiftKey: e.shiftKey,
						metaKey: e.metaKey,
						ctrlKey: e.ctrlKey,
						altKey: e.altKey
					})
				},
				o = t => {
					e.isPressed && (e.isOverTarget && r(yr(e.target, t), e.pointerType, !1), e.isPressed = !1, e.isOverTarget = !1, e.activePointerId = null, e.pointerType = null, y(), d || fr(e.target))
				},
				i = {
					onKeyDown(r) {
						vr(r.nativeEvent, r.currentTarget) && r.currentTarget.contains(r.target) ? (kr(r.target, r.key) && r.preventDefault(), r.stopPropagation(), e.isPressed || r.repeat || (e.target = r.currentTarget, e.isPressed = !0, t(r, "keyboard"), b(document, "keyup", c, !1))) : "Enter" === r.key && hr(r.currentTarget) && r.stopPropagation()
					},
					onKeyUp(t) {
						vr(t.nativeEvent, t.currentTarget) && !t.repeat && t.currentTarget.contains(t.target) && n(yr(e.target, t), "keyboard")
					},
					onClick(o) {
						o && !o.currentTarget.contains(o.target) || o && 0 === o.button && (o.stopPropagation(), a && o.preventDefault(), e.ignoreClickAfterPress || e.ignoreEmulatedMouseEvents || "virtual" !== e.pointerType && !xt(o.nativeEvent) || (a || s || pt(o.currentTarget), t(o, "virtual"), n(o, "virtual"), r(o, "virtual")), e.ignoreEmulatedMouseEvents = !1, e.ignoreClickAfterPress = !1)
					}
				},
				c = t => {
					if (e.isPressed && vr(t, e.target)) {
						kr(t.target, t.key) && t.preventDefault(), t.stopPropagation(), e.isPressed = !1;
						let n = t.target;
						r(yr(e.target, t), "keyboard", e.target.contains(n)), y(), e.target instanceof HTMLElement && e.target.contains(n) && (hr(e.target) || "link" === e.target.getAttribute("role")) && e.target.click()
					}
				};
			if ("undefined" != typeof PointerEvent) {
				i.onPointerDown = r => {
					var n;
					0 === r.button && r.currentTarget.contains(r.target) && (0 === (n = r.nativeEvent).width && 0 === n.height || 1 === n.width && 1 === n.height && 0 === n.pressure && 0 === n.detail && "mouse" === n.pointerType ? e.pointerType = "virtual" : (wr(r.currentTarget) && r.preventDefault(), e.pointerType = r.pointerType, r.stopPropagation(), e.isPressed || (e.isPressed = !0, e.isOverTarget = !0, e.activePointerId = r.pointerId, e.target = r.currentTarget, a || s || pt(r.currentTarget), d || pr(e.target), t(r, e.pointerType), b(document, "pointermove", c, !1), b(document, "pointerup", u, !1), b(document, "pointercancel", l, !1))))
				}, i.onMouseDown = e => {
					e.currentTarget.contains(e.target) && 0 === e.button && (wr(e.currentTarget) && e.preventDefault(), e.stopPropagation())
				}, i.onPointerUp = t => {
					t.currentTarget.contains(t.target) && "virtual" !== e.pointerType && 0 === t.button && _r(t, t.currentTarget) && n(t, e.pointerType || t.pointerType)
				};
				let c = n => {
						n.pointerId === e.activePointerId && (_r(n, e.target) ? e.isOverTarget || (e.isOverTarget = !0, t(yr(e.target, n), e.pointerType)) : e.isOverTarget && (e.isOverTarget = !1, r(yr(e.target, n), e.pointerType, !1), m.current.shouldCancelOnPointerExit && o(n)))
					},
					u = t => {
						t.pointerId === e.activePointerId && e.isPressed && 0 === t.button && (_r(t, e.target) ? r(yr(e.target, t), e.pointerType) : e.isOverTarget && r(yr(e.target, t), e.pointerType, !1), e.isPressed = !1, e.isOverTarget = !1, e.activePointerId = null, e.pointerType = null, y(), d || fr(e.target))
					},
					l = e => {
						o(e)
					};
				i.onDragStart = e => {
					e.currentTarget.contains(e.target) && o(e)
				}
			} else {
				i.onMouseDown = r => {
					0 === r.button && r.currentTarget.contains(r.target) && (wr(r.currentTarget) && r.preventDefault(), r.stopPropagation(), e.ignoreEmulatedMouseEvents || (e.isPressed = !0, e.isOverTarget = !0, e.target = r.currentTarget, e.pointerType = xt(r.nativeEvent) ? "virtual" : "mouse", a || s || pt(r.currentTarget), t(r, e.pointerType), b(document, "mouseup", c, !1)))
				}, i.onMouseEnter = r => {
					r.currentTarget.contains(r.target) && (r.stopPropagation(), e.isPressed && !e.ignoreEmulatedMouseEvents && (e.isOverTarget = !0, t(r, e.pointerType)))
				}, i.onMouseLeave = t => {
					t.currentTarget.contains(t.target) && (t.stopPropagation(), e.isPressed && !e.ignoreEmulatedMouseEvents && (e.isOverTarget = !1, r(t, e.pointerType, !1), m.current.shouldCancelOnPointerExit && o(t)))
				}, i.onMouseUp = t => {
					t.currentTarget.contains(t.target) && (e.ignoreEmulatedMouseEvents || 0 !== t.button || n(t, e.pointerType))
				};
				let c = t => {
					0 === t.button && (e.isPressed = !1, y(), e.ignoreEmulatedMouseEvents ? e.ignoreEmulatedMouseEvents = !1 : (_r(t, e.target) ? r(yr(e.target, t), e.pointerType) : e.isOverTarget && r(yr(e.target, t), e.pointerType, !1), e.isOverTarget = !1))
				};
				i.onTouchStart = r => {
					if (!r.currentTarget.contains(r.target)) return;
					r.stopPropagation();
					let n = function(e) {
						const {
							targetTouches: t
						} = e;
						return t.length > 0 ? t[0] : null
					}(r.nativeEvent);
					n && (e.activePointerId = n.identifier, e.ignoreEmulatedMouseEvents = !0, e.isOverTarget = !0, e.isPressed = !0, e.target = r.currentTarget, e.pointerType = "touch", a || s || pt(r.currentTarget), d || pr(e.target), t(r, e.pointerType), b(window, "scroll", u, !0))
				}, i.onTouchMove = n => {
					if (!n.currentTarget.contains(n.target)) return;
					if (n.stopPropagation(), !e.isPressed) return;
					let i = br(n.nativeEvent, e.activePointerId);
					i && _r(i, n.currentTarget) ? e.isOverTarget || (e.isOverTarget = !0, t(n, e.pointerType)) : e.isOverTarget && (e.isOverTarget = !1, r(n, e.pointerType, !1), m.current.shouldCancelOnPointerExit && o(n))
				}, i.onTouchEnd = t => {
					if (!t.currentTarget.contains(t.target)) return;
					if (t.stopPropagation(), !e.isPressed) return;
					let o = br(t.nativeEvent, e.activePointerId);
					o && _r(o, t.currentTarget) ? (n(t, e.pointerType), r(t, e.pointerType)) : e.isOverTarget && r(t, e.pointerType, !1), e.isPressed = !1, e.activePointerId = null, e.isOverTarget = !1, e.ignoreEmulatedMouseEvents = !0, d || fr(e.target), y()
				}, i.onTouchCancel = t => {
					t.currentTarget.contains(t.target) && (t.stopPropagation(), e.isPressed && o(t))
				};
				let u = t => {
					e.isPressed && t.target.contains(e.target) && o({
						currentTarget: e.target,
						shiftKey: !1,
						ctrlKey: !1,
						metaKey: !1,
						altKey: !1
					})
				};
				i.onDragStart = e => {
					e.currentTarget.contains(e.target) && o(e)
				}
			}
			return i
		}), [b, a, s, y, d]);
		return u.useEffect((() => () => {
			d || fr(v.current.target)
		}), [d]), {
			isPressed: c || g,
			pressProps: ct(f, _)
		}
	}

	function hr(e) {
		return "A" === e.tagName && e.hasAttribute("href")
	}

	function vr(e, t) {
		const {
			key: r,
			code: n
		} = e, o = t, i = o.getAttribute("role");
		return !("Enter" !== r && " " !== r && "Spacebar" !== r && "Space" !== n || o instanceof HTMLInputElement && !Tr(o, r) || o instanceof HTMLTextAreaElement || o.isContentEditable || hr(o) && ("button" !== i || "Enter" === r) || "link" === i && "Enter" !== r)
	}

	function br(e, t) {
		const r = e.changedTouches;
		for (let e = 0; e < r.length; e++) {
			const n = r[e];
			if (n.identifier === t) return n
		}
		return null
	}

	function yr(e, t) {
		return {
			currentTarget: e,
			shiftKey: t.shiftKey,
			ctrlKey: t.ctrlKey,
			metaKey: t.metaKey,
			altKey: t.altKey
		}
	}

	function _r(e, t) {
		let r = t.getBoundingClientRect(),
			n = function(e) {
				let t = e.width / 2 || e.radiusX || 0,
					r = e.height / 2 || e.radiusY || 0;
				return {
					top: e.clientY - r,
					right: e.clientX + t,
					bottom: e.clientY + r,
					left: e.clientX - t
				}
			}(e);
		return i = n, !((o = r).left > i.right || i.left > o.right || o.top > i.bottom || i.top > o.bottom);
		var o, i
	}

	function wr(e) {
		return !(e instanceof HTMLElement && e.draggable)
	}

	function kr(e, t) {
		return e instanceof HTMLInputElement ? !Tr(e, t) : !(e instanceof HTMLButtonElement) || "submit" !== e.type
	}
	mr.displayName = "PressResponderContext";
	const xr = new Set(["checkbox", "radio", "range", "color", "file", "image", "button", "submit", "reset"]);

	function Tr(e, t) {
		return "checkbox" === e.type || "radio" === e.type ? " " === t : xr.has(e.type)
	}
	class Sr {
		isDefaultPrevented() {
			return this.nativeEvent.defaultPrevented
		}
		preventDefault() {
			this.defaultPrevented = !0, this.nativeEvent.preventDefault()
		}
		stopPropagation() {
			this.nativeEvent.stopPropagation(), this.isPropagationStopped = () => !0
		}
		isPropagationStopped() {
			return !1
		}
		persist() {}
		constructor(e, t) {
			this.nativeEvent = t, this.target = t.target, this.currentTarget = t.currentTarget, this.relatedTarget = t.relatedTarget, this.bubbles = t.bubbles, this.cancelable = t.cancelable, this.defaultPrevented = t.defaultPrevented, this.eventPhase = t.eventPhase, this.isTrusted = t.isTrusted, this.timeStamp = t.timeStamp, this.type = e
		}
	}

	function Ir(e) {
		let t = u.useRef({
			isFocused: !1,
			onBlur: e,
			observer: null
		});
		return t.current.onBlur = e, tt((() => {
			const e = t.current;
			return () => {
				e.observer && (e.observer.disconnect(), e.observer = null)
			}
		}), []), u.useCallback((e => {
			if (e.target instanceof HTMLButtonElement || e.target instanceof HTMLInputElement || e.target instanceof HTMLTextAreaElement || e.target instanceof HTMLSelectElement) {
				t.current.isFocused = !0;
				let r = e.target,
					n = e => {
						var n, o;
						t.current.isFocused = !1, r.disabled && (null === (o = (n = t.current).onBlur) || void 0 === o || o.call(n, new Sr("blur", e))), t.current.observer && (t.current.observer.disconnect(), t.current.observer = null)
					};
				r.addEventListener("focusout", n, {
					once: !0
				}), t.current.observer = new MutationObserver((() => {
					t.current.isFocused && r.disabled && (t.current.observer.disconnect(), r.dispatchEvent(new FocusEvent("blur")), r.dispatchEvent(new FocusEvent("focusout", {
						bubbles: !0
					})))
				})), t.current.observer.observe(r, {
					attributes: !0,
					attributeFilter: ["disabled"]
				})
			}
		}), [])
	}

	function Er(e) {
		let {
			isDisabled: t,
			onFocus: r,
			onBlur: n,
			onFocusChange: o
		} = e;
		const i = u.useCallback((e => {
				if (e.target === e.currentTarget) return n && n(e), o && o(!1), !0
			}), [n, o]),
			a = Ir(i),
			c = u.useCallback((e => {
				e.target === e.currentTarget && (r && r(e), o && o(!0), a(e))
			}), [o, r, a]);
		return {
			focusProps: {
				onFocus: !t && (r || o || n) ? c : void 0,
				onBlur: t || !n && !o ? null : i
			}
		}
	}
	let Pr = null,
		Dr = new Set,
		Cr = !1,
		Ar = !1,
		Or = !1;
	const Br = {
		Tab: !0,
		Escape: !0
	};

	function Rr(e, t) {
		for (let r of Dr) r(e, t)
	}

	function Mr(e) {
		Ar = !0,
			function(e) {
				return !(e.metaKey || !_t() && e.altKey || e.ctrlKey || "Control" === e.key || "Shift" === e.key || "Meta" === e.key)
			}(e) && (Pr = "keyboard", Rr("keyboard", e))
	}

	function zr(e) {
		Pr = "pointer", "mousedown" !== e.type && "pointerdown" !== e.type || (Ar = !0, Rr("pointer", e))
	}

	function Fr(e) {
		xt(e) && (Ar = !0, Pr = "virtual")
	}

	function jr(e) {
		e.target !== window && e.target !== document && (Ar || Or || (Pr = "virtual", Rr("virtual", e)), Ar = !1, Or = !1)
	}

	function Lr() {
		Ar = !1, Or = !0
	}

	function Ur() {
		if ("undefined" == typeof window || Cr) return;
		let e = HTMLElement.prototype.focus;
		HTMLElement.prototype.focus = function() {
			Ar = !0, e.apply(this, arguments)
		}, document.addEventListener("keydown", Mr, !0), document.addEventListener("keyup", Mr, !0), document.addEventListener("click", Fr, !0), window.addEventListener("focus", jr, !0), window.addEventListener("blur", Lr, !1), "undefined" != typeof PointerEvent ? (document.addEventListener("pointerdown", zr, !0), document.addEventListener("pointermove", zr, !0), document.addEventListener("pointerup", zr, !0)) : (document.addEventListener("mousedown", zr, !0), document.addEventListener("mousemove", zr, !0), document.addEventListener("mouseup", zr, !0)), Cr = !0
	}

	function Nr() {
		return "pointer" !== Pr
	}

	function Hr(e, t, r) {
		Ur(), u.useEffect((() => {
			let t = (t, n) => {
				(function(e, t, r) {
					return !(e && "keyboard" === t && r instanceof KeyboardEvent && !Br[r.key])
				})(null == r ? void 0 : r.isTextInput, t, n) && e(Nr())
			};
			return Dr.add(t), () => {
				Dr.delete(t)
			}
		}), t)
	}
	"undefined" != typeof document && ("loading" !== document.readyState ? Ur() : document.addEventListener("DOMContentLoaded", Ur));
	let Vr = !1,
		qr = 0;

	function Wr() {
		Vr = !0, setTimeout((() => {
			Vr = !1
		}), 50)
	}

	function Gr(e) {
		"touch" === e.pointerType && Wr()
	}

	function Kr() {
		if ("undefined" != typeof document) return "undefined" != typeof PointerEvent ? document.addEventListener("pointerup", Gr) : document.addEventListener("touchend", Wr), qr++, () => {
			qr--, qr > 0 || ("undefined" != typeof PointerEvent ? document.removeEventListener("pointerup", Gr) : document.removeEventListener("touchend", Wr))
		}
	}

	function $r(e) {
		if (!e) return;
		let t = !0;
		return r => {
			let n = {
				...r,
				preventDefault() {
					r.preventDefault()
				},
				isDefaultPrevented: () => r.isDefaultPrevented(),
				stopPropagation() {
					console.error("stopPropagation is now the default behavior for events in React Spectrum. You can use continuePropagation() to revert this behavior.")
				},
				continuePropagation() {
					t = !1
				}
			};
			e(n), t && r.stopPropagation()
		}
	}

	function Qr(e) {
		if ("virtual" === Pr) {
			let t = document.activeElement;
			vt((() => {
				document.activeElement === t && document.contains(e) && pt(e)
			}))
		} else pt(e)
	}

	function Xr(e, t) {
		return t.some((t => t.contains(e)))
	}
	class Yr {
		get size() {
			return this.fastMap.size
		}
		getTreeNode(e) {
			return this.fastMap.get(e)
		}
		addTreeNode(e, t, r) {
			let n = this.fastMap.get(null != t ? t : null),
				o = new Jr({
					scopeRef: e
				});
			n.addChild(o), o.parent = n, this.fastMap.set(e, o), r && (o.nodeToRestore = r)
		}
		removeTreeNode(e) {
			if (null === e) return;
			let t = this.fastMap.get(e),
				r = t.parent;
			for (let e of this.traverse()) e !== t && t.nodeToRestore && e.nodeToRestore && t.scopeRef.current && Xr(e.nodeToRestore, t.scopeRef.current) && (e.nodeToRestore = t.nodeToRestore);
			let n = t.children;
			r.removeChild(t), n.length > 0 && n.forEach((e => r.addChild(e))), this.fastMap.delete(t.scopeRef)
		}* traverse(e = this.root) {
			if (null != e.scopeRef && (yield e), e.children.length > 0)
				for (let t of e.children) yield* this.traverse(t)
		}
		clone() {
			let e = new Yr;
			for (let t of this.traverse()) e.addTreeNode(t.scopeRef, t.parent.scopeRef, t.nodeToRestore);
			return e
		}
		constructor() {
			Qe(this, "fastMap", new Map), this.root = new Jr({
				scopeRef: null
			}), this.fastMap.set(null, this.root)
		}
	}
	class Jr {
		addChild(e) {
			this.children.push(e), e.parent = this
		}
		removeChild(e) {
			this.children.splice(this.children.indexOf(e), 1), e.parent = void 0
		}
		constructor(e) {
			Qe(this, "children", []), Qe(this, "contain", !1), this.scopeRef = e.scopeRef
		}
	}

	function Zr(e = {}) {
		let {
			autoFocus: t = !1,
			isTextInput: r,
			within: n
		} = e, o = u.useRef({
			isFocused: !1,
			isFocusVisible: t || Nr()
		}), [i, a] = u.useState(!1), [c, s] = u.useState((() => o.current.isFocused && o.current.isFocusVisible)), l = u.useCallback((() => s(o.current.isFocused && o.current.isFocusVisible)), []), d = u.useCallback((e => {
			o.current.isFocused = e, a(e), l()
		}), [l]);
		Hr((e => {
			o.current.isFocusVisible = e, l()
		}), [], {
			isTextInput: r
		});
		let {
			focusProps: p
		} = Er({
			isDisabled: n,
			onFocusChange: d
		}), {
			focusWithinProps: f
		} = function(e) {
			let {
				isDisabled: t,
				onBlurWithin: r,
				onFocusWithin: n,
				onFocusWithinChange: o
			} = e, i = u.useRef({
				isFocusWithin: !1
			}), a = u.useCallback((e => {
				i.current.isFocusWithin && !e.currentTarget.contains(e.relatedTarget) && (i.current.isFocusWithin = !1, r && r(e), o && o(!1))
			}), [r, o, i]), c = Ir(a), s = u.useCallback((e => {
				i.current.isFocusWithin || (n && n(e), o && o(!0), i.current.isFocusWithin = !0, c(e))
			}), [n, o, c]);
			return t ? {
				focusWithinProps: {
					onFocus: null,
					onBlur: null
				}
			} : {
				focusWithinProps: {
					onFocus: s,
					onBlur: a
				}
			}
		}({
			isDisabled: !n,
			onFocusWithinChange: d
		});
		return {
			isFocused: i,
			isFocusVisible: o.current.isFocused && c,
			focusProps: n ? f : p
		}
	}

	function en(e) {
		let {
			children: t,
			focusClass: r,
			focusRingClass: n
		} = e, {
			isFocused: o,
			isFocusVisible: i,
			focusProps: a
		} = Zr(e), c = h.Children.only(t);
		return h.cloneElement(c, ct(c.props, {
			...a,
			className: et({
				[r || ""]: o,
				[n || ""]: i
			})
		}))
	}
	new Yr;
	let tn = h.createContext(null);

	function rn(e, t) {
		let {
			focusProps: r
		} = Er(e), {
			keyboardProps: n
		} = function(e) {
			return {
				keyboardProps: e.isDisabled ? {} : {
					onKeyDown: $r(e.onKeyDown),
					onKeyUp: $r(e.onKeyUp)
				}
			}
		}(e), o = ct(r, n), i = function(e) {
			let t = u.useContext(tn) || {};
			bt(t, e);
			let {
				ref: r,
				...n
			} = t;
			return n
		}(t), a = e.isDisabled ? {} : i, c = u.useRef(e.autoFocus);
		return u.useEffect((() => {
			c.current && t.current && Qr(t.current), c.current = !1
		}), [t]), {
			focusableProps: ct({
				...o,
				tabIndex: e.excludeFromTabOrder && !e.isDisabled ? -1 : void 0
			}, a)
		}
	}

	function nn(e, t) {
		e = ar(e, "text");
		let {
			children: r,
			...n
		} = e, {
			styleProps: o
		} = rr(n), i = qt(t);
		return h.createElement("span", {
			...dt(n),
			...o,
			ref: i
		}, r)
	}
	const on = u.forwardRef(nn);
	Ne(".spectrum_b37d53{background-color:var(--spectrum-alias-background-color-default,var(--spectrum-global-color-gray-100));-webkit-tap-highlight-color:#0000}.spectrum_2a241c{font-family:adobe-clean-ux,adobe-clean,Source Sans Pro,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum_2a241c:lang(ar){font-family:adobe-arabic,myriad-arabic,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum_2a241c:lang(he){font-family:adobe-hebrew,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum_2a241c:lang(zh-Hans),.spectrum_2a241c:lang(zh){font-family:adobe-clean-han-simplified-c,SimSun,Heiti SC Light,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum_2a241c:lang(ko){font-family:adobe-clean-han-korean,Malgun Gothic,Apple Gothic,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum_2a241c:lang(ja){font-family:adobe-clean-han-japanese,Yu Gothic,メイリオ,ヒラギノ角ゴ Pro W3,Hiragino Kaku Gothic Pro W3,Osaka,ＭＳＰゴシック,MS PGothic,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif}.spectrum_2a241c{font-size:var(--spectrum-alias-font-size-default,var(--spectrum-global-dimension-font-size-100));color:var(--spectrum-global-color-gray-800)}.spectrum_2a241c,.spectrum_2a241c.spectrum-Body_2a241c,.spectrum-Body_2a241c{font-size:var(--spectrum-body-4-text-size,var(--spectrum-alias-font-size-default));font-weight:var(--spectrum-body-4-text-font-weight,var(--spectrum-alias-body-text-font-weight));line-height:var(--spectrum-body-4-text-line-height,var(--spectrum-alias-body-text-line-height));font-style:var(--spectrum-body-4-text-font-style,var(--spectrum-global-font-style-regular))}.spectrum-Body--italic_2a241c{font-style:var(--spectrum-body-4-emphasis-text-font-style,var(--spectrum-global-font-style-italic))}", {});
	const an = h.createContext(null);

	function cn(e, t, r, n) {
		Object.defineProperty(e, t, {
			get: r,
			set: n,
			enumerable: !0,
			configurable: !0
		})
	}
	var sn;
	cn({}, "spectrum", (() => sn), (e => sn = e)), sn = "spectrum_b37d53";
	var un, ln, dn, pn = {};
	cn(pn, "spectrum", (() => un), (e => un = e)), cn(pn, "spectrum-Body", (() => ln), (e => ln = e)), cn(pn, "spectrum-Body--italic", (() => dn), (e => dn = e)), un = "spectrum_2a241c", ln = "spectrum-Body_2a241c", dn = "spectrum-Body--italic_2a241c", JSON.parse('{"name":"@react-spectrum/provider","version":"3.6.1","description":"Spectrum UI components in React","license":"Apache-2.0","main":"dist/main.js","module":"dist/module.js","types":"dist/types.d.ts","source":"src/index.ts","files":["dist","src"],"sideEffects":["*.css"],"targets":{"main":{"includeNodeModules":["@adobe/spectrum-css-temp"]},"module":{"includeNodeModules":["@adobe/spectrum-css-temp"]}},"repository":{"type":"git","url":"https://github.com/adobe/react-spectrum"},"dependencies":{"@react-aria/i18n":"^3.6.3","@react-aria/overlays":"^3.12.1","@react-aria/utils":"^3.14.2","@react-spectrum/utils":"^3.8.1","@react-types/provider":"^3.5.5","@react-types/shared":"^3.16.0","@swc/helpers":"^0.4.14","clsx":"^1.1.1"},"devDependencies":{"@adobe/spectrum-css-temp":"3.0.0-alpha.1"},"peerDependencies":{"react":"^16.8.0 || ^17.0.0-rc.1 || ^18.0.0","react-dom":"^16.8.0 || ^17.0.0-rc.1 || ^18.0.0"},"publishConfig":{"access":"public"}}');
	const fn = h.createContext(null);

	function mn() {
		return u.useContext(fn)
	}
	fn.displayName = "ProviderContext";
	var gn = {};
	! function(e) {
		e.exports = function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		}, e.exports.__esModule = !0, e.exports.default = e.exports
	}({
		get exports() {
			return gn
		},
		set exports(e) {
			gn = e
		}
	});
	var hn = {};
	Object.defineProperty(hn, "__esModule", {
		value: !0
	}), hn.CornerTriangle = wn;
	var vn, bn = (vn = u) && vn.__esModule ? vn : {
		default: vn
	};

	function yn() {
		return yn = Object.assign || function(e) {
			for (var t = 1; t < arguments.length; t++) {
				var r = arguments[t];
				for (var n in r) Object.prototype.hasOwnProperty.call(r, n) && (e[n] = r[n])
			}
			return e
		}, yn.apply(this, arguments)
	}

	function _n(e, t) {
		if (null == e) return {};
		var r, n, o = function(e, t) {
			if (null == e) return {};
			var r, n, o = {},
				i = Object.keys(e);
			for (n = 0; n < i.length; n++) r = i[n], t.indexOf(r) >= 0 || (o[r] = e[r]);
			return o
		}(e, t);
		if (Object.getOwnPropertySymbols) {
			var i = Object.getOwnPropertySymbols(e);
			for (n = 0; n < i.length; n++) r = i[n], t.indexOf(r) >= 0 || Object.prototype.propertyIsEnumerable.call(e, r) && (o[r] = e[r])
		}
		return o
	}

	function wn(e) {
		var t = e.scale,
			r = void 0 === t ? "M" : t,
			n = _n(e, ["scale"]);
		return bn.default.createElement("svg", yn({}, n, n), "L" === r && bn.default.createElement("path", {
			d: "M5.74.01a.25.25 0 0 0-.177.073l-5.48 5.48a.25.25 0 0 0 .177.427h5.48a.25.25 0 0 0 .25-.25V.26a.25.25 0 0 0-.25-.25z"
		}), "M" === r && bn.default.createElement("path", {
			d: "M4.74.01a.25.25 0 0 0-.177.073l-4.48 4.48a.25.25 0 0 0 .177.427h4.48a.25.25 0 0 0 .25-.25V.26a.25.25 0 0 0-.25-.25z"
		}))
	}
	wn.displayName = "CornerTriangle";

	function kn(e) {
		return e && e.__esModule ? e.default : e
	}

	function xn(e, t, r, n) {
		Object.defineProperty(e, t, {
			get: r,
			set: n,
			enumerable: !0,
			configurable: !0
		})
	}
	Ne(".spectrum-Icon_368b34,.spectrum-UIIcon_368b34{color:inherit;fill:currentColor;pointer-events:none;display:inline-block}.spectrum-Icon_368b34:not(:root),.spectrum-UIIcon_368b34:not(:root){overflow:hidden}@media (forced-colors:active){.spectrum-Icon_368b34,.spectrum-UIIcon_368b34{forced-color-adjust:auto}}.spectrum-Icon--sizeXXS_368b34,.spectrum-Icon--sizeXXS_368b34 img,.spectrum-Icon--sizeXXS_368b34 svg{height:calc(var(--spectrum-alias-workflow-icon-size,var(--spectrum-global-dimension-size-225))/2);width:calc(var(--spectrum-alias-workflow-icon-size,var(--spectrum-global-dimension-size-225))/2)}.spectrum-Icon--sizeXS_368b34,.spectrum-Icon--sizeXS_368b34 img,.spectrum-Icon--sizeXS_368b34 svg{height:calc(var(--spectrum-global-dimension-size-300)/2);width:calc(var(--spectrum-global-dimension-size-300)/2)}.spectrum-Icon--sizeS_368b34,.spectrum-Icon--sizeS_368b34 img,.spectrum-Icon--sizeS_368b34 svg{height:var(--spectrum-alias-workflow-icon-size,var(--spectrum-global-dimension-size-225));width:var(--spectrum-alias-workflow-icon-size,var(--spectrum-global-dimension-size-225))}.spectrum-Icon--sizeM_368b34,.spectrum-Icon--sizeM_368b34 img,.spectrum-Icon--sizeM_368b34 svg{height:var(--spectrum-global-dimension-size-300);width:var(--spectrum-global-dimension-size-300)}.spectrum-Icon--sizeL_368b34,.spectrum-Icon--sizeL_368b34 img,.spectrum-Icon--sizeL_368b34 svg{height:calc(var(--spectrum-alias-workflow-icon-size,var(--spectrum-global-dimension-size-225))*2);width:calc(var(--spectrum-alias-workflow-icon-size,var(--spectrum-global-dimension-size-225))*2)}.spectrum-Icon--sizeXL_368b34,.spectrum-Icon--sizeXL_368b34 img,.spectrum-Icon--sizeXL_368b34 svg{height:calc(var(--spectrum-global-dimension-size-300)*2);width:calc(var(--spectrum-global-dimension-size-300)*2)}.spectrum-Icon--sizeXXL_368b34,.spectrum-Icon--sizeXXL_368b34 img,.spectrum-Icon--sizeXXL_368b34 svg{height:calc(var(--spectrum-global-dimension-size-300)*3);width:calc(var(--spectrum-global-dimension-size-300)*3)}.spectrum--medium_368b34 .spectrum-UIIcon--large_368b34{display:none}.spectrum--medium_368b34 .spectrum-UIIcon--medium_368b34{display:inline}.spectrum--large_368b34 .spectrum-UIIcon--medium_368b34{display:none}.spectrum--large_368b34 .spectrum-UIIcon--large_368b34{display:inline}.spectrum--large_368b34{--ui-icon-large-display:block;--ui-icon-medium-display:none}.spectrum--medium_368b34{--ui-icon-medium-display:block;--ui-icon-large-display:none}.spectrum-UIIcon--large_368b34{display:var(--ui-icon-large-display)}.spectrum-UIIcon--medium_368b34{display:var(--ui-icon-medium-display)}.spectrum-UIIcon-AlertMedium_368b34{width:var(--spectrum-icon-alert-medium-width,var(--spectrum-global-dimension-size-225));height:var(--spectrum-icon-alert-medium-height,var(--spectrum-global-dimension-size-225))}.spectrum-UIIcon-AlertSmall_368b34{width:var(--spectrum-icon-alert-small-width,var(--spectrum-global-dimension-size-175));height:var(--spectrum-icon-alert-small-height,var(--spectrum-global-dimension-size-175))}.spectrum-UIIcon-ArrowDownSmall_368b34{width:var(--spectrum-icon-arrow-down-small-width,var(--spectrum-global-dimension-size-100));height:var(--spectrum-icon-arrow-down-small-height)}.spectrum-UIIcon-ArrowLeftMedium_368b34{width:var(--spectrum-icon-arrow-left-medium-width,var(--spectrum-global-dimension-size-175));height:var(--spectrum-icon-arrow-left-medium-height)}.spectrum-UIIcon-Asterisk_368b34{width:var(--spectrum-fieldlabel-asterisk-size,var(--spectrum-global-dimension-size-100));height:var(--spectrum-fieldlabel-asterisk-size,var(--spectrum-global-dimension-size-100))}.spectrum-UIIcon-CheckmarkMedium_368b34{width:var(--spectrum-icon-checkmark-medium-width);height:var(--spectrum-icon-checkmark-medium-height)}.spectrum-UIIcon-CheckmarkSmall_368b34{width:var(--spectrum-icon-checkmark-small-width);height:var(--spectrum-icon-checkmark-small-height)}.spectrum-UIIcon-ChevronDownMedium_368b34{width:var(--spectrum-icon-chevron-down-medium-width);height:var(--spectrum-icon-chevron-down-medium-height,var(--spectrum-global-dimension-size-75))}.spectrum-UIIcon-ChevronDownSmall_368b34{width:var(--spectrum-icon-chevron-down-small-width,var(--spectrum-global-dimension-size-100));height:var(--spectrum-icon-chevron-down-small-height,var(--spectrum-global-dimension-size-75))}.spectrum-UIIcon-ChevronLeftLarge_368b34{width:var(--spectrum-icon-chevron-left-large-width);height:var(--spectrum-icon-chevron-left-large-height,var(--spectrum-global-dimension-size-200))}.spectrum-UIIcon-ChevronLeftMedium_368b34{width:var(--spectrum-icon-chevron-left-medium-width,var(--spectrum-global-dimension-size-75));height:var(--spectrum-icon-chevron-left-medium-height)}.spectrum-UIIcon-ChevronRightLarge_368b34{width:var(--spectrum-icon-chevron-right-large-width);height:var(--spectrum-icon-chevron-right-large-height,var(--spectrum-global-dimension-size-200))}.spectrum-UIIcon-ChevronRightMedium_368b34{width:var(--spectrum-icon-chevron-right-medium-width,var(--spectrum-global-dimension-size-75));height:var(--spectrum-icon-chevron-right-medium-height)}.spectrum-UIIcon-ChevronRightSmall_368b34{width:var(--spectrum-icon-chevron-right-small-width,var(--spectrum-global-dimension-size-75));height:var(--spectrum-icon-chevron-right-small-height,var(--spectrum-global-dimension-size-100))}.spectrum-UIIcon-ChevronUpSmall_368b34{width:var(--spectrum-icon-chevron-up-small-width,var(--spectrum-global-dimension-size-100));height:var(--spectrum-icon-chevron-up-small-height,var(--spectrum-global-dimension-size-75))}.spectrum-UIIcon-CornerTriangle_368b34{width:var(--spectrum-icon-cornertriangle-width,var(--spectrum-global-dimension-size-65));height:var(--spectrum-icon-cornertriangle-height,var(--spectrum-global-dimension-size-65))}.spectrum-UIIcon-CrossLarge_368b34{width:var(--spectrum-icon-cross-large-width);height:var(--spectrum-icon-cross-large-height)}.spectrum-UIIcon-CrossMedium_368b34{width:var(--spectrum-icon-cross-medium-width,var(--spectrum-global-dimension-size-100));height:var(--spectrum-icon-cross-medium-height,var(--spectrum-global-dimension-size-100))}.spectrum-UIIcon-CrossSmall_368b34{width:var(--spectrum-icon-cross-small-width,var(--spectrum-global-dimension-size-100));height:var(--spectrum-icon-cross-small-height,var(--spectrum-global-dimension-size-100))}.spectrum-UIIcon-DashSmall_368b34{width:var(--spectrum-icon-dash-small-width);height:var(--spectrum-icon-dash-small-height)}.spectrum-UIIcon-DoubleGripper_368b34{width:var(--spectrum-icon-doublegripper-width,var(--spectrum-global-dimension-size-200));height:var(--spectrum-icon-doublegripper-height,var(--spectrum-global-dimension-size-50))}.spectrum-UIIcon-FolderBreadcrumb_368b34{width:var(--spectrum-icon-folderbreadcrumb-width,var(--spectrum-global-dimension-size-225));height:var(--spectrum-icon-folderbreadcrumb-height,var(--spectrum-global-dimension-size-225))}.spectrum-UIIcon-HelpMedium_368b34{width:var(--spectrum-icon-info-medium-width,var(--spectrum-global-dimension-size-225));height:var(--spectrum-icon-info-medium-height,var(--spectrum-global-dimension-size-225))}.spectrum-UIIcon-HelpSmall_368b34{width:var(--spectrum-icon-info-small-width,var(--spectrum-global-dimension-size-175));height:var(--spectrum-icon-info-small-height,var(--spectrum-global-dimension-size-175))}.spectrum-UIIcon-InfoMedium_368b34{width:var(--spectrum-icon-info-medium-width,var(--spectrum-global-dimension-size-225));height:var(--spectrum-icon-info-medium-height,var(--spectrum-global-dimension-size-225))}.spectrum-UIIcon-InfoSmall_368b34{width:var(--spectrum-icon-info-small-width,var(--spectrum-global-dimension-size-175));height:var(--spectrum-icon-info-small-height,var(--spectrum-global-dimension-size-175))}.spectrum-UIIcon-ListGripper_368b34{width:var(--spectrum-global-dimension-size-65);height:var(--spectrum-global-dimension-size-150)}.spectrum-UIIcon-Magnifier_368b34{width:var(--spectrum-icon-magnifier-width,var(--spectrum-global-dimension-size-200));height:var(--spectrum-icon-magnifier-height,var(--spectrum-global-dimension-size-200))}.spectrum-UIIcon-SkipLeft_368b34{width:var(--spectrum-icon-skip-left-width);height:var(--spectrum-icon-skip-left-height)}.spectrum-UIIcon-SkipRight_368b34{width:var(--spectrum-icon-skip-right-width);height:var(--spectrum-icon-skip-right-height)}.spectrum-UIIcon-Star_368b34{width:var(--spectrum-icon-star-width,var(--spectrum-global-dimension-size-225));height:var(--spectrum-icon-star-height,var(--spectrum-global-dimension-size-225))}.spectrum-UIIcon-StarOutline_368b34{width:var(--spectrum-icon-star-outline-width,var(--spectrum-global-dimension-size-225));height:var(--spectrum-icon-star-outline-height,var(--spectrum-global-dimension-size-225))}.spectrum-UIIcon-SuccessMedium_368b34{width:var(--spectrum-icon-success-medium-width,var(--spectrum-global-dimension-size-225));height:var(--spectrum-icon-success-medium-height,var(--spectrum-global-dimension-size-225))}.spectrum-UIIcon-SuccessSmall_368b34{width:var(--spectrum-icon-success-small-width,var(--spectrum-global-dimension-size-175));height:var(--spectrum-icon-success-small-height,var(--spectrum-global-dimension-size-175))}.spectrum-UIIcon-TripleGripper_368b34{width:var(--spectrum-icon-triplegripper-width);height:var(--spectrum-icon-triplegripper-height,var(--spectrum-global-dimension-size-85))}", {});
	var Tn, Sn, In, En, Pn, Dn, Cn, An, On, Bn, Rn, Mn, zn, Fn, jn, Ln, Un, Nn, Hn, Vn, qn, Wn, Gn, Kn, $n, Qn, Xn, Yn, Jn, Zn, eo, to, ro, no, oo, io, ao, co, so, uo, lo, po, fo, mo, go, ho, vo, bo, yo = {};
	xn(yo, "spectrum-Icon", (() => Tn), (e => Tn = e)), xn(yo, "spectrum-UIIcon", (() => Sn), (e => Sn = e)), xn(yo, "spectrum-Icon--sizeXXS", (() => In), (e => In = e)), xn(yo, "spectrum-Icon--sizeXS", (() => En), (e => En = e)), xn(yo, "spectrum-Icon--sizeS", (() => Pn), (e => Pn = e)), xn(yo, "spectrum-Icon--sizeM", (() => Dn), (e => Dn = e)), xn(yo, "spectrum-Icon--sizeL", (() => Cn), (e => Cn = e)), xn(yo, "spectrum-Icon--sizeXL", (() => An), (e => An = e)), xn(yo, "spectrum-Icon--sizeXXL", (() => On), (e => On = e)), xn(yo, "spectrum--medium", (() => Bn), (e => Bn = e)), xn(yo, "spectrum-UIIcon--large", (() => Rn), (e => Rn = e)), xn(yo, "spectrum-UIIcon--medium", (() => Mn), (e => Mn = e)), xn(yo, "spectrum--large", (() => zn), (e => zn = e)), xn(yo, "spectrum-UIIcon-AlertMedium", (() => Fn), (e => Fn = e)), xn(yo, "spectrum-UIIcon-AlertSmall", (() => jn), (e => jn = e)), xn(yo, "spectrum-UIIcon-ArrowDownSmall", (() => Ln), (e => Ln = e)), xn(yo, "spectrum-UIIcon-ArrowLeftMedium", (() => Un), (e => Un = e)), xn(yo, "spectrum-UIIcon-Asterisk", (() => Nn), (e => Nn = e)), xn(yo, "spectrum-UIIcon-CheckmarkMedium", (() => Hn), (e => Hn = e)), xn(yo, "spectrum-UIIcon-CheckmarkSmall", (() => Vn), (e => Vn = e)), xn(yo, "spectrum-UIIcon-ChevronDownMedium", (() => qn), (e => qn = e)), xn(yo, "spectrum-UIIcon-ChevronDownSmall", (() => Wn), (e => Wn = e)), xn(yo, "spectrum-UIIcon-ChevronLeftLarge", (() => Gn), (e => Gn = e)), xn(yo, "spectrum-UIIcon-ChevronLeftMedium", (() => Kn), (e => Kn = e)), xn(yo, "spectrum-UIIcon-ChevronRightLarge", (() => $n), (e => $n = e)), xn(yo, "spectrum-UIIcon-ChevronRightMedium", (() => Qn), (e => Qn = e)), xn(yo, "spectrum-UIIcon-ChevronRightSmall", (() => Xn), (e => Xn = e)), xn(yo, "spectrum-UIIcon-ChevronUpSmall", (() => Yn), (e => Yn = e)), xn(yo, "spectrum-UIIcon-CornerTriangle", (() => Jn), (e => Jn = e)), xn(yo, "spectrum-UIIcon-CrossLarge", (() => Zn), (e => Zn = e)), xn(yo, "spectrum-UIIcon-CrossMedium", (() => eo), (e => eo = e)), xn(yo, "spectrum-UIIcon-CrossSmall", (() => to), (e => to = e)), xn(yo, "spectrum-UIIcon-DashSmall", (() => ro), (e => ro = e)), xn(yo, "spectrum-UIIcon-DoubleGripper", (() => no), (e => no = e)), xn(yo, "spectrum-UIIcon-FolderBreadcrumb", (() => oo), (e => oo = e)), xn(yo, "spectrum-UIIcon-HelpMedium", (() => io), (e => io = e)), xn(yo, "spectrum-UIIcon-HelpSmall", (() => ao), (e => ao = e)), xn(yo, "spectrum-UIIcon-InfoMedium", (() => co), (e => co = e)), xn(yo, "spectrum-UIIcon-InfoSmall", (() => so), (e => so = e)), xn(yo, "spectrum-UIIcon-ListGripper", (() => uo), (e => uo = e)), xn(yo, "spectrum-UIIcon-Magnifier", (() => lo), (e => lo = e)), xn(yo, "spectrum-UIIcon-SkipLeft", (() => po), (e => po = e)), xn(yo, "spectrum-UIIcon-SkipRight", (() => fo), (e => fo = e)), xn(yo, "spectrum-UIIcon-Star", (() => mo), (e => mo = e)), xn(yo, "spectrum-UIIcon-StarOutline", (() => go), (e => go = e)), xn(yo, "spectrum-UIIcon-SuccessMedium", (() => ho), (e => ho = e)), xn(yo, "spectrum-UIIcon-SuccessSmall", (() => vo), (e => vo = e)), xn(yo, "spectrum-UIIcon-TripleGripper", (() => bo), (e => bo = e)), Tn = "spectrum-Icon_368b34", Sn = "spectrum-UIIcon_368b34", In = "spectrum-Icon--sizeXXS_368b34", En = "spectrum-Icon--sizeXS_368b34", Pn = "spectrum-Icon--sizeS_368b34", Dn = "spectrum-Icon--sizeM_368b34", Cn = "spectrum-Icon--sizeL_368b34", An = "spectrum-Icon--sizeXL_368b34", On = "spectrum-Icon--sizeXXL_368b34", Bn = "spectrum--medium_368b34", Rn = "spectrum-UIIcon--large_368b34", Mn = "spectrum-UIIcon--medium_368b34", zn = "spectrum--large_368b34", Fn = "spectrum-UIIcon-AlertMedium_368b34", jn = "spectrum-UIIcon-AlertSmall_368b34", Ln = "spectrum-UIIcon-ArrowDownSmall_368b34", Un = "spectrum-UIIcon-ArrowLeftMedium_368b34", Nn = "spectrum-UIIcon-Asterisk_368b34", Hn = "spectrum-UIIcon-CheckmarkMedium_368b34", Vn = "spectrum-UIIcon-CheckmarkSmall_368b34", qn = "spectrum-UIIcon-ChevronDownMedium_368b34", Wn = "spectrum-UIIcon-ChevronDownSmall_368b34", Gn = "spectrum-UIIcon-ChevronLeftLarge_368b34", Kn = "spectrum-UIIcon-ChevronLeftMedium_368b34", $n = "spectrum-UIIcon-ChevronRightLarge_368b34", Qn = "spectrum-UIIcon-ChevronRightMedium_368b34", Xn = "spectrum-UIIcon-ChevronRightSmall_368b34", Yn = "spectrum-UIIcon-ChevronUpSmall_368b34", Jn = "spectrum-UIIcon-CornerTriangle_368b34", Zn = "spectrum-UIIcon-CrossLarge_368b34", eo = "spectrum-UIIcon-CrossMedium_368b34", to = "spectrum-UIIcon-CrossSmall_368b34", ro = "spectrum-UIIcon-DashSmall_368b34", no = "spectrum-UIIcon-DoubleGripper_368b34", oo = "spectrum-UIIcon-FolderBreadcrumb_368b34", io = "spectrum-UIIcon-HelpMedium_368b34", ao = "spectrum-UIIcon-HelpSmall_368b34", co = "spectrum-UIIcon-InfoMedium_368b34", so = "spectrum-UIIcon-InfoSmall_368b34", uo = "spectrum-UIIcon-ListGripper_368b34", lo = "spectrum-UIIcon-Magnifier_368b34", po = "spectrum-UIIcon-SkipLeft_368b34", fo = "spectrum-UIIcon-SkipRight_368b34", mo = "spectrum-UIIcon-Star_368b34", go = "spectrum-UIIcon-StarOutline_368b34", ho = "spectrum-UIIcon-SuccessMedium_368b34", vo = "spectrum-UIIcon-SuccessSmall_368b34", bo = "spectrum-UIIcon-TripleGripper_368b34";
	const _o = {
		...$t,
		color: ["color", function(e) {
			return `var(--spectrum-semantic-${e}-color-icon)`
		}]
	};
	var wo = n(Object.freeze({
			__proto__: null,
			Icon: function(e) {
				e = ar(e, "icon");
				let {
					children: t,
					size: r,
					"aria-label": n,
					"aria-hidden": o,
					...i
				} = e, {
					styleProps: a
				} = rr(i, _o), c = mn(), s = "M";
				null !== c && (s = "large" === c.scale ? "L" : "M"), o || (o = void 0);
				let u = r || s;
				return h.cloneElement(t, {
					...dt(i),
					...a,
					focusable: "false",
					"aria-label": n,
					"aria-hidden": !n || (o || void 0),
					role: "img",
					className: Ht(kn(yo), t.props.className, "spectrum-Icon", `spectrum-Icon--size${u}`, a.className)
				})
			},
			Illustration: function(e) {
				e = ar(e, "illustration");
				let {
					children: t,
					"aria-label": r,
					"aria-labelledby": n,
					"aria-hidden": o,
					...i
				} = e, {
					styleProps: a
				} = rr(i), c = r || n;
				return o || (o = void 0), h.cloneElement(t, {
					...dt(i),
					...a,
					focusable: "false",
					"aria-label": r,
					"aria-labelledby": n,
					"aria-hidden": o,
					role: c ? "img" : void 0
				})
			},
			UIIcon: function(e) {
				e = ar(e, "icon");
				let {
					children: t,
					"aria-label": r,
					"aria-hidden": n,
					...o
				} = e, {
					styleProps: i
				} = rr(o), a = mn(), c = "M";
				return null !== a && (c = "large" === a.scale ? "L" : "M"), n || (n = void 0), h.cloneElement(t, {
					...dt(o),
					...i,
					scale: c,
					focusable: "false",
					"aria-label": r,
					"aria-hidden": !r || (n || void 0),
					role: "img",
					className: Ht(kn(yo), t.props.className, "spectrum-Icon", {
						[`spectrum-UIIcon-${t.type.displayName}`]: t.type.displayName
					}, i.className)
				})
			}
		})),
		ko = function(e) {
			return So.default.createElement(To.UIIcon, e, So.default.createElement(xo.CornerTriangle, null))
		},
		xo = hn,
		To = wo,
		So = gn(u);
	var Io = {};
	Object.defineProperty(Io, "__esModule", {
		value: !0
	}), Io.CrossSmall = Co;
	var Eo = function(e) {
		return e && e.__esModule ? e : {
			default: e
		}
	}(u);

	function Po() {
		return Po = Object.assign || function(e) {
			for (var t = 1; t < arguments.length; t++) {
				var r = arguments[t];
				for (var n in r) Object.prototype.hasOwnProperty.call(r, n) && (e[n] = r[n])
			}
			return e
		}, Po.apply(this, arguments)
	}

	function Do(e, t) {
		if (null == e) return {};
		var r, n, o = function(e, t) {
			if (null == e) return {};
			var r, n, o = {},
				i = Object.keys(e);
			for (n = 0; n < i.length; n++) r = i[n], t.indexOf(r) >= 0 || (o[r] = e[r]);
			return o
		}(e, t);
		if (Object.getOwnPropertySymbols) {
			var i = Object.getOwnPropertySymbols(e);
			for (n = 0; n < i.length; n++) r = i[n], t.indexOf(r) >= 0 || Object.prototype.propertyIsEnumerable.call(e, r) && (o[r] = e[r])
		}
		return o
	}

	function Co(e) {
		var t = e.scale,
			r = void 0 === t ? "M" : t,
			n = Do(e, ["scale"]);
		return Eo.default.createElement("svg", Po({}, n, n), "L" === r && Eo.default.createElement("path", {
			d: "M9.317 8.433L5.884 5l3.433-3.433a.625.625 0 1 0-.884-.884L5 4.116 1.567.683a.625.625 0 1 0-.884.884C.83 1.713 2.77 3.657 4.116 5L.683 8.433a.625.625 0 1 0 .884.884L5 5.884l3.433 3.433a.625.625 0 0 0 .884-.884z"
		}), "M" === r && Eo.default.createElement("path", {
			d: "M7.317 6.433L4.884 4l2.433-2.433a.625.625 0 1 0-.884-.884L4 3.116 1.567.683a.625.625 0 1 0-.884.884L3.116 4 .683 6.433a.625.625 0 1 0 .884.884L4 4.884l2.433 2.433a.625.625 0 0 0 .884-.884z"
		}))
	}

	function Ao(e) {
		return e && e.__esModule ? e.default : e
	}

	function Oo(e, t, r, n) {
		Object.defineProperty(e, t, {
			get: r,
			set: n,
			enumerable: !0,
			configurable: !0
		})
	}
	Co.displayName = "CrossSmall", gn(u);
	var Bo, Ro, Mo, zo, Fo, jo, Lo, Uo, No, Ho, Vo, qo, Wo, Go, Ko, $o, Qo, Xo, Yo, Jo, Zo, ei, ti, ri, ni, oi, ii, ai, ci, si = {};

	function ui(e, t) {
		e = ar(e = function(e) {
			let t = mn();
			return t ? Object.assign({}, {
				isQuiet: t.isQuiet,
				isEmphasized: t.isEmphasized,
				isDisabled: t.isDisabled,
				isRequired: t.isRequired,
				isReadOnly: t.isReadOnly,
				validationState: t.validationState
			}, e) : e
		}(e), "actionButton");
		let {
			isQuiet: r,
			isDisabled: n,
			staticColor: o,
			children: i,
			autoFocus: a,
			holdAffordance: c,
			...s
		} = e, l = Wt(t), {
			buttonProps: d,
			isPressed: p
		} = function(e, t) {
			let r, {
				elementType: n = "button",
				isDisabled: o,
				onPress: i,
				onPressStart: a,
				onPressEnd: c,
				onPressChange: s,
				preventFocusOnPress: u,
				allowFocusWhenDisabled: l,
				onClick: d,
				href: p,
				target: f,
				rel: m,
				type: g = "button"
			} = e;
			r = "button" === n ? {
				type: g,
				disabled: o
			} : {
				role: "button",
				tabIndex: o ? void 0 : 0,
				href: "a" === n && o ? void 0 : p,
				target: "a" === n ? f : void 0,
				type: "input" === n ? g : void 0,
				disabled: "input" === n ? o : void 0,
				"aria-disabled": o && "input" !== n ? o : void 0,
				rel: "a" === n ? m : void 0
			};
			let {
				pressProps: h,
				isPressed: v
			} = gr({
				onPressStart: a,
				onPressEnd: c,
				onPressChange: s,
				onPress: i,
				isDisabled: o,
				preventFocusOnPress: u,
				ref: t
			}), {
				focusableProps: b
			} = rn(e, t);
			l && (b.tabIndex = o ? -1 : b.tabIndex);
			let y = ct(b, h, dt(e, {
				labelable: !0
			}));
			return {
				isPressed: v,
				buttonProps: ct(r, y, {
					"aria-haspopup": e["aria-haspopup"],
					"aria-expanded": e["aria-expanded"],
					"aria-controls": e["aria-controls"],
					"aria-pressed": e["aria-pressed"],
					onClick: e => {
						d && (d(e), console.warn("onClick is deprecated, please use onPress"))
					}
				})
			}
		}(e, l), {
			hoverProps: f,
			isHovered: m
		} = function(e) {
			let {
				onHoverStart: t,
				onHoverChange: r,
				onHoverEnd: n,
				isDisabled: o
			} = e, [i, a] = u.useState(!1), c = u.useRef({
				isHovered: !1,
				ignoreEmulatedMouseEvents: !1,
				pointerType: "",
				target: null
			}).current;
			u.useEffect(Kr, []);
			let {
				hoverProps: s,
				triggerHoverEnd: l
			} = u.useMemo((() => {
				let e = (e, n) => {
						if (c.pointerType = n, o || "touch" === n || c.isHovered || !e.currentTarget.contains(e.target)) return;
						c.isHovered = !0;
						let i = e.currentTarget;
						c.target = i, t && t({
							type: "hoverstart",
							target: i,
							pointerType: n
						}), r && r(!0), a(!0)
					},
					i = (e, t) => {
						if (c.pointerType = "", c.target = null, "touch" === t || !c.isHovered) return;
						c.isHovered = !1;
						let o = e.currentTarget;
						n && n({
							type: "hoverend",
							target: o,
							pointerType: t
						}), r && r(!1), a(!1)
					},
					s = {};
				return "undefined" != typeof PointerEvent ? (s.onPointerEnter = t => {
					Vr && "mouse" === t.pointerType || e(t, t.pointerType)
				}, s.onPointerLeave = e => {
					!o && e.currentTarget.contains(e.target) && i(e, e.pointerType)
				}) : (s.onTouchStart = () => {
					c.ignoreEmulatedMouseEvents = !0
				}, s.onMouseEnter = t => {
					c.ignoreEmulatedMouseEvents || Vr || e(t, "mouse"), c.ignoreEmulatedMouseEvents = !1
				}, s.onMouseLeave = e => {
					!o && e.currentTarget.contains(e.target) && i(e, "mouse")
				}), {
					hoverProps: s,
					triggerHoverEnd: i
				}
			}), [t, r, n, o, c]);
			return u.useEffect((() => {
				o && l({
					currentTarget: c.target
				}, c.pointerType)
			}), [o]), {
				hoverProps: s,
				isHovered: i
			}
		}({
			isDisabled: n
		}), {
			styleProps: g
		} = rr(s), v = h.Children.toArray(e.children).every((e => !h.isValidElement(e)));
		return h.createElement(en, {
			focusRingClass: Ht(Ao(si), "focus-ring"),
			autoFocus: a
		}, h.createElement("button", {
			...g,
			...ct(d, f),
			ref: l,
			className: Ht(Ao(si), "spectrum-ActionButton", {
				"spectrum-ActionButton--quiet": r,
				"spectrum-ActionButton--staticColor": !!o,
				"spectrum-ActionButton--staticWhite": "white" === o,
				"spectrum-ActionButton--staticBlack": "black" === o,
				"is-active": p,
				"is-disabled": n,
				"is-hovered": m
			}, g.className)
		}, c && h.createElement(ko, {
			UNSAFE_className: Ht(Ao(si), "spectrum-ActionButton-hold")
		}), h.createElement(cr, {
			slots: {
				icon: {
					size: "S",
					UNSAFE_className: Ht(Ao(si), "spectrum-Icon")
				},
				text: {
					UNSAFE_className: Ht(Ao(si), "spectrum-ActionButton-label")
				}
			}
		}, "string" == typeof i || v ? h.createElement(on, null, i) : i)))
	}
	Oo(si, "spectrum-Button", (() => Bo), (e => Bo = e)), Oo(si, "spectrum-ActionButton", (() => Ro), (e => Ro = e)), Oo(si, "spectrum-LogicButton", (() => Mo), (e => Mo = e)), Oo(si, "spectrum-FieldButton", (() => zo), (e => zo = e)), Oo(si, "spectrum-ClearButton", (() => Fo), (e => Fo = e)), Oo(si, "focus-ring", (() => jo), (e => jo = e)), Oo(si, "spectrum-FieldButton--quiet", (() => Lo), (e => Lo = e)), Oo(si, "spectrum-Icon", (() => Uo), (e => Uo = e)), Oo(si, "is-hovered", (() => No), (e => No = e)), Oo(si, "spectrum-Button-label", (() => Ho), (e => Ho = e)), Oo(si, "spectrum-Button--iconOnly", (() => Vo), (e => Vo = e)), Oo(si, "spectrum-ActionButton-label", (() => qo), (e => qo = e)), Oo(si, "spectrum-ActionButton-hold", (() => Wo), (e => Wo = e)), Oo(si, "spectrum-ActionButton--quiet", (() => Go), (e => Go = e)), Oo(si, "is-disabled", (() => Ko), (e => Ko = e)), Oo(si, "is-open", (() => $o), (e => $o = e)), Oo(si, "spectrum-ClearButton--small", (() => Qo), (e => Qo = e)), Oo(si, "is-active", (() => Xo), (e => Xo = e)), Oo(si, "is-selected", (() => Yo), (e => Yo = e)), Oo(si, "spectrum-ActionButton--emphasized", (() => Jo), (e => Jo = e)), Oo(si, "spectrum-ActionButton--staticWhite", (() => Zo), (e => Zo = e)), Oo(si, "spectrum-ActionButton--staticBlack", (() => ei), (e => ei = e)), Oo(si, "spectrum-ActionButton--staticColor", (() => ti), (e => ti = e)), Oo(si, "spectrum-LogicButton--and", (() => ri), (e => ri = e)), Oo(si, "spectrum-LogicButton--or", (() => ni), (e => ni = e)), Oo(si, "is-focused", (() => oi), (e => oi = e)), Oo(si, "is-placeholder", (() => ii), (e => ii = e)), Oo(si, "spectrum-FieldButton--invalid", (() => ai), (e => ai = e)), Oo(si, "spectrum-Button--overBackground", (() => ci), (e => ci = e)), Bo = "spectrum-Button_e2d99e", Ro = "spectrum-ActionButton_e2d99e", Mo = "spectrum-LogicButton_e2d99e", zo = "spectrum-FieldButton_e2d99e", Fo = "spectrum-ClearButton_e2d99e", jo = "focus-ring_e2d99e", Lo = "spectrum-FieldButton--quiet_e2d99e", Uo = "spectrum-Icon_e2d99e", No = "is-hovered_e2d99e", Ho = "spectrum-Button-label_e2d99e", Vo = "spectrum-Button--iconOnly_e2d99e", qo = "spectrum-ActionButton-label_e2d99e", Wo = "spectrum-ActionButton-hold_e2d99e", Go = "spectrum-ActionButton--quiet_e2d99e", Ko = "is-disabled_e2d99e", $o = "is-open_e2d99e", Qo = "spectrum-ClearButton--small_e2d99e", Xo = "is-active_e2d99e", Yo = "is-selected_e2d99e", Jo = "spectrum-ActionButton--emphasized_e2d99e", Zo = "spectrum-ActionButton--staticWhite_e2d99e", ei = "spectrum-ActionButton--staticBlack_e2d99e", ti = "spectrum-ActionButton--staticColor_e2d99e", ri = "spectrum-LogicButton--and_e2d99e", ni = "spectrum-LogicButton--or_e2d99e", oi = "is-focused_e2d99e", ii = "is-placeholder_e2d99e", ai = "spectrum-FieldButton--invalid_e2d99e", ci = "spectrum-Button--overBackground_e2d99e";
	let li = h.forwardRef(ui);

	function di(e, t, r, n) {
		Object.defineProperty(e, t, {
			get: r,
			set: n,
			enumerable: !0,
			configurable: !0
		})
	}
	Ne(".flex-container_e15493,.flex_e15493{display:flex}.flex-gap_e15493{--gap:0px;--column-gap:var(--gap);--row-gap:var(--gap);margin:calc(var(--row-gap)/-2)calc(var(--column-gap)/-2);width:calc(100% + var(--column-gap) + 1px);height:calc(100% + var(--row-gap))}.flex-container_e15493 .flex-gap_e15493>*{margin:calc(var(--row-gap)/2)calc(var(--column-gap)/2)}", {});
	const pi = {
		...$t,
		autoFlow: ["gridAutoFlow", nr],
		autoColumns: ["gridAutoColumns", mi],
		autoRows: ["gridAutoRows", mi],
		areas: ["gridTemplateAreas", function(e) {
			return e.map((e => `"${e}"`)).join("\n")
		}],
		columns: ["gridTemplateColumns", gi],
		rows: ["gridTemplateRows", gi],
		gap: ["gap", er],
		rowGap: ["rowGap", er],
		columnGap: ["columnGap", er],
		justifyItems: ["justifyItems", nr],
		justifyContent: ["justifyContent", nr],
		alignItems: ["alignItems", nr],
		alignContent: ["alignContent", nr]
	};

	function fi(e, t) {
		let {
			children: r,
			...n
		} = e, {
			styleProps: o
		} = rr(n, pi);
		o.style.display = "grid";
		let i = qt(t);
		return h.createElement("div", {
			...dt(n),
			...o,
			ref: i
		}, r)
	}

	function mi(e) {
		return /^max-content|min-content|minmax|auto|fit-content|repeat|subgrid/.test(e) ? e : er(e)
	}

	function gi(e) {
		return Array.isArray(e) ? e.map(mi).join(" ") : mi(e)
	}
	const hi = u.forwardRef(fi);
	var vi, bi, yi, _i = {};
	di(_i, "flex-container", (() => vi), (e => vi = e)), di(_i, "flex", (() => bi), (e => bi = e)), di(_i, "flex-gap", (() => yi), (e => yi = e)), vi = "flex-container_e15493", bi = "flex_e15493", yi = "flex-gap_e15493";
	var wi, ki = {};
	var xi, Ti = {};
	var Si = gn;
	Si((wi || (wi = 1, function(e) {
		function t() {
			return e.exports = t = Object.assign ? Object.assign.bind() : function(e) {
				for (var t = 1; t < arguments.length; t++) {
					var r = arguments[t];
					for (var n in r) Object.prototype.hasOwnProperty.call(r, n) && (e[n] = r[n])
				}
				return e
			}, e.exports.__esModule = !0, e.exports.default = e.exports, t.apply(this, arguments)
		}
		e.exports = t, e.exports.__esModule = !0, e.exports.default = e.exports
	}({
		get exports() {
			return ki
		},
		set exports(e) {
			ki = e
		}
	})), ki));
	var Ii = function() {
		if (xi) return Ti;
		xi = 1, Object.defineProperty(Ti, "__esModule", {
			value: !0
		}), Ti.AlertMedium = n;
		var e = function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		}(u);

		function t() {
			return t = Object.assign || function(e) {
				for (var t = 1; t < arguments.length; t++) {
					var r = arguments[t];
					for (var n in r) Object.prototype.hasOwnProperty.call(r, n) && (e[n] = r[n])
				}
				return e
			}, t.apply(this, arguments)
		}

		function r(e, t) {
			if (null == e) return {};
			var r, n, o = function(e, t) {
				if (null == e) return {};
				var r, n, o = {},
					i = Object.keys(e);
				for (n = 0; n < i.length; n++) r = i[n], t.indexOf(r) >= 0 || (o[r] = e[r]);
				return o
			}(e, t);
			if (Object.getOwnPropertySymbols) {
				var i = Object.getOwnPropertySymbols(e);
				for (n = 0; n < i.length; n++) r = i[n], t.indexOf(r) >= 0 || Object.prototype.propertyIsEnumerable.call(e, r) && (o[r] = e[r])
			}
			return o
		}

		function n(n) {
			var o = n.scale,
				i = void 0 === o ? "M" : o,
				a = r(n, ["scale"]);
			return e.default.createElement("svg", t({}, a, a), "L" === i && e.default.createElement("path", {
				d: "M10.563 2.206l-9.249 16.55a.5.5 0 0 0 .436.744h18.5a.5.5 0 0 0 .436-.744l-9.251-16.55a.5.5 0 0 0-.872 0zm1.436 15.044a.25.25 0 0 1-.25.25h-1.5a.25.25 0 0 1-.25-.25v-1.5a.25.25 0 0 1 .25-.25h1.5a.25.25 0 0 1 .25.25zm0-3.5a.25.25 0 0 1-.25.25h-1.5a.25.25 0 0 1-.25-.25v-6a.25.25 0 0 1 .25-.25h1.5a.25.25 0 0 1 .25.25z"
			}), "M" === i && e.default.createElement("path", {
				d: "M8.564 1.289L.2 16.256A.5.5 0 0 0 .636 17h16.728a.5.5 0 0 0 .436-.744L9.436 1.289a.5.5 0 0 0-.872 0zM10 14.75a.25.25 0 0 1-.25.25h-1.5a.25.25 0 0 1-.25-.25v-1.5a.25.25 0 0 1 .25-.25h1.5a.25.25 0 0 1 .25.25zm0-3a.25.25 0 0 1-.25.25h-1.5a.25.25 0 0 1-.25-.25v-6a.25.25 0 0 1 .25-.25h1.5a.25.25 0 0 1 .25.25z"
			}))
		}
		return n.displayName = "AlertMedium", Ti
	}();

	function Ei(e, t) {
		let {
			role: r = "dialog"
		} = e, n = it();
		n = e["aria-label"] ? void 0 : n;
		let o = u.useRef(!1);
		return u.useEffect((() => {
				if (t.current && !t.current.contains(document.activeElement)) {
					Qr(t.current);
					let e = setTimeout((() => {
						document.activeElement === t.current && (o.current = !0, t.current.blur(), Qr(t.current), o.current = !1)
					}), 500);
					return () => {
						clearTimeout(e)
					}
				}
			}), [t]),
			function() {
				let e = u.useContext(an),
					t = null == e ? void 0 : e.setContain;
				tt((() => {
					null == t || t(!0)
				}), [t])
			}(), {
				dialogProps: {
					...dt(e, {
						labelable: !0
					}),
					role: r,
					tabIndex: -1,
					"aria-labelledby": e["aria-labelledby"] || n,
					onBlur: e => {
						o.current && e.stopPropagation()
					}
				},
				titleProps: {
					id: n
				}
			}
	}
	Si(u), Ii.AlertMedium.displayName;
	Ne('.spectrum-Dialog_6d8b48{box-sizing:border-box;width:-moz-fit-content;width:-moz-fit-content;width:fit-content;min-width:var(--spectrum-dialog-min-width,var(--spectrum-global-dimension-static-size-3600));max-width:100%;max-height:inherit;--spectrum-dialog-padding-x:var(--spectrum-dialog-padding);--spectrum-dialog-padding-y:var(--spectrum-dialog-padding);--spectrum-dialog-border-radius:var(--spectrum-alias-border-radius-regular,var(--spectrum-global-dimension-size-50));outline:none;display:flex}.spectrum-Dialog--small_6d8b48{width:400px}.spectrum-Dialog--medium_6d8b48{width:480px}.spectrum-Dialog--large_6d8b48{width:640px}.spectrum-Dialog-hero_6d8b48{height:var(--spectrum-global-dimension-size-1600);border-top-left-radius:var(--spectrum-dialog-border-radius,var(--spectrum-global-dimension-size-50));border-top-right-radius:var(--spectrum-dialog-border-radius,var(--spectrum-global-dimension-size-50));background-position:50%;background-size:cover;grid-area:hero;overflow:hidden}.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48{display:-ms-grid;-ms-grid-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)var(--spectrum-dialog-padding-x);grid-template-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)var(--spectrum-dialog-padding-x);-ms-grid-rows:auto var(--spectrum-dialog-padding-y)auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-rows:auto var(--spectrum-dialog-padding-y)auto auto 1fr auto var(--spectrum-dialog-padding-y);width:100%;grid-template-areas:"hero hero hero hero hero hero"". . . . . ."".heading header header typeIcon."".divider divider divider divider."".content content content content."".footer footer buttonGroup buttonGroup."". . . . . .";display:grid}[dir=ltr] .spectrum-Dialog-heading_6d8b48{padding-right:var(--spectrum-global-dimension-size-200)}[dir=rtl] .spectrum-Dialog-heading_6d8b48{padding-left:var(--spectrum-global-dimension-size-200)}.spectrum-Dialog-heading_6d8b48{font-size:var(--spectrum-dialog-title-text-size);font-weight:var(--spectrum-dialog-title-text-font-weight,var(--spectrum-global-font-weight-bold));line-height:var(--spectrum-dialog-title-text-line-height,var(--spectrum-alias-heading-text-line-height));outline:none;grid-area:heading;margin:0}[dir=ltr] .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{padding-left:0}.spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{grid-area:heading-start/heading-start/header-end/header-end}[dir=ltr] .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{padding-left:0}.spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{grid-area:heading-start/heading-start/typeIcon-end/typeIcon-end}.spectrum-Dialog-header_6d8b48{box-sizing:border-box;min-width:-moz-fit-content;min-width:-moz-fit-content;min-width:fit-content;outline:none;grid-area:header;justify-content:flex-end;align-items:center;display:flex}[dir=ltr] .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{padding-left:0}.spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{grid-area:header-start/header-start/typeIcon-end/typeIcon-end}.spectrum-Dialog-typeIcon_6d8b48{grid-area:typeIcon}.spectrum-Dialog_6d8b48 .spectrum-Dialog-divider_6d8b48{width:100%;margin-top:var(--spectrum-dialog-rule-margin-top,var(--spectrum-global-dimension-static-size-150));margin-bottom:var(--spectrum-dialog-rule-margin-bottom,var(--spectrum-global-dimension-static-size-200));grid-area:divider}.spectrum-Dialog--noDivider_6d8b48 .spectrum-Dialog-divider_6d8b48{display:none}.spectrum-Dialog-content_6d8b48{box-sizing:border-box;-webkit-overflow-scrolling:touch;font-size:var(--spectrum-dialog-content-text-size);font-weight:var(--spectrum-dialog-content-text-font-weight,var(--spectrum-global-font-weight-regular));line-height:var(--spectrum-dialog-content-text-line-height,var(--spectrum-alias-body-text-line-height));padding:calc(var(--spectrum-global-dimension-size-25)*2);margin:calc(var(--spectrum-global-dimension-size-25)*-2);min-height:var(--spectrum-alias-single-line-height,var(--spectrum-global-dimension-size-400));outline:none;grid-area:content;overflow-y:auto}.spectrum-Dialog-footer_6d8b48{padding-top:var(--spectrum-global-dimension-static-size-500,40px);outline:none;flex-wrap:wrap;grid-area:footer;display:flex}.spectrum-Dialog-footer_6d8b48>*,.spectrum-Dialog-footer_6d8b48>.spectrum-Button_6d8b48+.spectrum-Button_6d8b48{margin-bottom:0}[dir=ltr] .spectrum-Dialog-buttonGroup_6d8b48{padding-left:var(--spectrum-global-dimension-size-200)}[dir=rtl] .spectrum-Dialog-buttonGroup_6d8b48{padding-right:var(--spectrum-global-dimension-size-200)}.spectrum-Dialog-buttonGroup_6d8b48{padding-top:var(--spectrum-global-dimension-static-size-500,40px);max-width:100%;grid-area:buttonGroup;justify-content:flex-end;display:flex}.spectrum-Dialog-buttonGroup_6d8b48.spectrum-Dialog-buttonGroup--noFooter_6d8b48{grid-area:footer-start/footer-start/buttonGroup-end/buttonGroup-end}.spectrum-Dialog_6d8b48.spectrum-Dialog--dismissable_6d8b48 .spectrum-Dialog-grid_6d8b48{-ms-grid-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)minmax(0,var(--spectrum-global-dimension-size-400))var(--spectrum-dialog-padding-x);grid-template-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)minmax(0,var(--spectrum-global-dimension-size-400))var(--spectrum-dialog-padding-x);-ms-grid-rows:auto var(--spectrum-dialog-padding-y)auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-rows:auto var(--spectrum-dialog-padding-y)auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-areas:"hero hero hero hero hero hero hero"". . . . .closeButton closeButton"".heading header header typeIcon closeButton closeButton"".divider divider divider divider divider."".content content content content content."".footer footer buttonGroup buttonGroup buttonGroup."". . . . . . ."}.spectrum-Dialog_6d8b48.spectrum-Dialog--dismissable_6d8b48 .spectrum-Dialog-grid_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48{display:none}.spectrum-Dialog_6d8b48.spectrum-Dialog--dismissable_6d8b48 .spectrum-Dialog-grid_6d8b48 .spectrum-Dialog-footer_6d8b48{grid-area:footer/footer/buttonGroup/buttonGroup}[dir=ltr] .spectrum-Dialog_6d8b48.spectrum-Dialog--dismissable_6d8b48 .spectrum-Dialog-closeButton_6d8b48{margin-right:calc(26px - var(--spectrum-global-dimension-size-175))}[dir=rtl] .spectrum-Dialog_6d8b48.spectrum-Dialog--dismissable_6d8b48 .spectrum-Dialog-closeButton_6d8b48{margin-left:calc(26px - var(--spectrum-global-dimension-size-175))}.spectrum-Dialog_6d8b48.spectrum-Dialog--dismissable_6d8b48 .spectrum-Dialog-closeButton_6d8b48{margin-top:calc(26px - var(--spectrum-global-dimension-size-175));grid-area:closeButton;place-self:flex-start end}.spectrum-Dialog--error_6d8b48{width:480px}.spectrum-Dialog--fullscreen_6d8b48{width:100%;height:100%}.spectrum-Dialog--fullscreenTakeover_6d8b48{width:100%;height:100%;border-radius:0}.spectrum-Dialog--fullscreen_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48{max-height:none;max-width:none}.spectrum-Dialog--fullscreen_6d8b48.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48{display:-ms-grid;-ms-grid-columns:var(--spectrum-dialog-padding-x)1fr auto auto var(--spectrum-dialog-padding-x);grid-template-columns:var(--spectrum-dialog-padding-x)1fr auto auto var(--spectrum-dialog-padding-x);-ms-grid-rows:var(--spectrum-dialog-padding-y)auto auto 1fr var(--spectrum-dialog-padding-y);grid-template-rows:var(--spectrum-dialog-padding-y)auto auto 1fr var(--spectrum-dialog-padding-y);grid-template-areas:". . . . ."".heading header buttonGroup."".divider divider divider."".content content content."". . . . .";display:grid}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48{font-size:28px}[dir=ltr] .spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48,[dir=ltr] .spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48,[dir=rtl] .spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{padding-left:0}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{grid-area:heading-start/heading-start/header-end/header-end}[dir=ltr] .spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,[dir=ltr] .spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,[dir=rtl] .spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{padding-left:0}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{grid-area:heading-start/heading-start/header-end/header-end}[dir=ltr] .spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48,[dir=ltr] .spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48,[dir=rtl] .spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{padding-left:0}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{grid-area:header}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-content_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-content_6d8b48{max-height:none}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-footer_6d8b48,.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-footer_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48{padding-top:0}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-footer_6d8b48,.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-typeIcon_6d8b48,.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-closeButton_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-footer_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-typeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-closeButton_6d8b48{display:none}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48{grid-area:buttonGroup}@media screen and (max-width:700px){.spectrum-Dialog_6d8b48{--spectrum-dialog-padding:var(--spectrum-global-dimension-static-size-300,24px)}.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48{-ms-grid-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)var(--spectrum-dialog-padding-x);grid-template-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)var(--spectrum-dialog-padding-x);-ms-grid-rows:auto var(--spectrum-dialog-padding-y)auto auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-rows:auto var(--spectrum-dialog-padding-y)auto auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-areas:"hero hero hero hero hero hero"". . . . . ."".heading heading heading typeIcon."".header header header header."".divider divider divider divider."".content content content content."".footer footer buttonGroup buttonGroup."". . . . . ."}.spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{grid-area:heading}.spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{grid-area:heading-start/heading-start/typeIcon-end/typeIcon-end}.spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{grid-area:header}.spectrum-Dialog_6d8b48.spectrum-Dialog--dismissable_6d8b48 .spectrum-Dialog-grid_6d8b48{-ms-grid-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)minmax(0,var(--spectrum-global-dimension-size-400))var(--spectrum-dialog-padding-x);grid-template-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)minmax(0,var(--spectrum-global-dimension-size-400))var(--spectrum-dialog-padding-x);-ms-grid-rows:auto var(--spectrum-dialog-padding-y)auto auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-rows:auto var(--spectrum-dialog-padding-y)auto auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-areas:"hero hero hero hero hero hero hero"". . . . .closeButton closeButton"".heading heading heading typeIcon closeButton closeButton"".header header header header header."".divider divider divider divider divider."".content content content content content."".footer footer buttonGroup buttonGroup buttonGroup."". . . . . . ."}.spectrum-Dialog_6d8b48 .spectrum-Dialog-header_6d8b48{justify-content:flex-start}.spectrum-Dialog-footer_6d8b48{min-width:-moz-fit-content;min-width:-moz-fit-content;min-width:fit-content}.spectrum-Dialog-buttonGroup_6d8b48{min-width:0}.spectrum-Dialog--fullscreen_6d8b48.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48{display:-ms-grid;-ms-grid-columns:var(--spectrum-dialog-padding-x)1fr var(--spectrum-dialog-padding-x);grid-template-columns:var(--spectrum-dialog-padding-x)1fr var(--spectrum-dialog-padding-x);-ms-grid-rows:var(--spectrum-dialog-padding-y)auto auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-rows:var(--spectrum-dialog-padding-y)auto auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-areas:". . ."".heading."".header."".divider."".content."".buttonGroup."". . .";display:grid}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48,.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{grid-area:heading}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{grid-area:header}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48{padding-top:var(--spectrum-global-dimension-static-size-500,40px)}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48{font-size:var(--spectrum-dialog-title-text-size)}}@media screen and (max-height:400px){.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48{border-top-left-radius:var(--spectrum-dialog-border-radius,var(--spectrum-global-dimension-size-50));border-top-right-radius:var(--spectrum-dialog-border-radius,var(--spectrum-global-dimension-size-50));-ms-grid-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)var(--spectrum-dialog-padding-x);grid-template-columns:var(--spectrum-dialog-padding-x)auto 1fr auto minmax(0,auto)var(--spectrum-dialog-padding-x);-ms-grid-rows:auto var(--spectrum-dialog-padding-y)auto auto auto 1fr auto auto var(--spectrum-dialog-padding-y);grid-template-rows:auto var(--spectrum-dialog-padding-y)auto auto auto 1fr auto auto var(--spectrum-dialog-padding-y);grid-template-areas:"hero hero hero hero hero hero"". . . . . ."".heading heading heading typeIcon."".header header header header."".divider divider divider divider."".content content content content."".footer footer footer footer."".buttonGroup buttonGroup buttonGroup buttonGroup."". . . . . .";overflow-y:auto}[dir=ltr] .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{padding-left:0}.spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48{grid-area:heading}[dir=ltr] .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{padding-left:0}.spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{grid-area:heading-start/heading-start/typeIcon-end/typeIcon-end}[dir=ltr] .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{padding-right:0}[dir=rtl] .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{padding-left:0}.spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{grid-area:header}.spectrum-Dialog-content_6d8b48{height:min-content;display:inline-table;overflow-y:visible}.spectrum-Dialog-footer_6d8b48+.spectrum-Dialog-buttonGroup_6d8b48{padding-top:calc(var(--spectrum-global-dimension-size-25)*2)}}@media screen and (max-height:400px) and (max-width:700px){.spectrum-Dialog--fullscreen_6d8b48.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48.spectrum-Dialog_6d8b48 .spectrum-Dialog-grid_6d8b48{display:-ms-grid;-ms-grid-columns:var(--spectrum-dialog-padding-x)1fr var(--spectrum-dialog-padding-x);grid-template-columns:var(--spectrum-dialog-padding-x)1fr var(--spectrum-dialog-padding-x);-ms-grid-rows:var(--spectrum-dialog-padding-y)auto auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-rows:var(--spectrum-dialog-padding-y)auto auto auto 1fr auto var(--spectrum-dialog-padding-y);grid-template-areas:". . ."".heading."".header."".divider."".content."".buttonGroup."". . .";display:grid}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48,.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48.spectrum-Dialog-heading--noHeader_6d8b48.spectrum-Dialog-heading--noTypeIcon_6d8b48{grid-area:heading}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-header_6d8b48.spectrum-Dialog-header--noTypeIcon_6d8b48{grid-area:header}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-buttonGroup_6d8b48{padding-top:var(--spectrum-global-dimension-static-size-500,40px)}.spectrum-Dialog--fullscreen_6d8b48 .spectrum-Dialog-heading_6d8b48,.spectrum-Dialog--fullscreenTakeover_6d8b48 .spectrum-Dialog-heading_6d8b48{font-size:var(--spectrum-dialog-title-text-size)}}.spectrum-Dialog-heading_6d8b48{color:var(--spectrum-dialog-title-text-color,var(--spectrum-global-color-gray-900))}.spectrum-Dialog-content_6d8b48{color:var(--spectrum-dialog-content-text-color,var(--spectrum-global-color-gray-800))}.spectrum-Dialog-typeIcon_6d8b48{color:var(--spectrum-dialog-icon-color,var(--spectrum-global-color-gray-900))}.spectrum-Dialog--error_6d8b48 .spectrum-Dialog-typeIcon_6d8b48{color:var(--spectrum-dialog-error-icon-color,var(--spectrum-semantic-negative-color-icon))}.spectrum-Dialog--warning_6d8b48 .spectrum-Dialog-typeIcon_6d8b48{color:var(--spectrum-semantic-notice-color-icon,var(--spectrum-global-color-orange-600))}@media (forced-colors:active){.spectrum-Dialog_6d8b48{border:1px solid #0000}}', {});
	var Pi = {};
	Object.defineProperty(Pi, "__esModule", {
		value: !0
	}), Pi.CrossLarge = Oi;
	var Di = function(e) {
		return e && e.__esModule ? e : {
			default: e
		}
	}(u);

	function Ci() {
		return Ci = Object.assign || function(e) {
			for (var t = 1; t < arguments.length; t++) {
				var r = arguments[t];
				for (var n in r) Object.prototype.hasOwnProperty.call(r, n) && (e[n] = r[n])
			}
			return e
		}, Ci.apply(this, arguments)
	}

	function Ai(e, t) {
		if (null == e) return {};
		var r, n, o = function(e, t) {
			if (null == e) return {};
			var r, n, o = {},
				i = Object.keys(e);
			for (n = 0; n < i.length; n++) r = i[n], t.indexOf(r) >= 0 || (o[r] = e[r]);
			return o
		}(e, t);
		if (Object.getOwnPropertySymbols) {
			var i = Object.getOwnPropertySymbols(e);
			for (n = 0; n < i.length; n++) r = i[n], t.indexOf(r) >= 0 || Object.prototype.propertyIsEnumerable.call(e, r) && (o[r] = e[r])
		}
		return o
	}

	function Oi(e) {
		var t = e.scale,
			r = void 0 === t ? "M" : t,
			n = Ai(e, ["scale"]);
		return Di.default.createElement("svg", Ci({}, n, n), "L" === r && Di.default.createElement("path", {
			d: "M15.697 14.283L9.414 8l6.283-6.283A1 1 0 1 0 14.283.303L8 6.586 1.717.303A1 1 0 1 0 .303 1.717L6.586 8 .303 14.283a1 1 0 1 0 1.414 1.414L8 9.414l6.283 6.283a1 1 0 1 0 1.414-1.414z"
		}), "M" === r && Di.default.createElement("path", {
			d: "M11.697 10.283L7.414 6l4.283-4.283A1 1 0 1 0 10.283.303L6 4.586 1.717.303A1 1 0 1 0 .303 1.717L4.586 6 .303 10.283a1 1 0 1 0 1.414 1.414L6 7.414l4.283 4.283a1 1 0 1 0 1.414-1.414z"
		}))
	}
	Oi.displayName = "CrossLarge";
	var Bi = function(e) {
			return zi.default.createElement(Mi.UIIcon, e, zi.default.createElement(Ri.CrossLarge, null))
		},
		Ri = Pi,
		Mi = wo,
		zi = gn(u);

	function Fi(e) {
		return e && e.__esModule ? e.default : e
	}

	function ji(e, t, r, n) {
		Object.defineProperty(e, t, {
			get: r,
			set: n,
			enumerable: !0,
			configurable: !0
		})
	}
	const Li = h.createContext(null);
	var Ui = {};
	Ui = {
		"ar-AE": {
			alert: "تنبيه",
			dismiss: "تجاهل"
		},
		"bg-BG": {
			alert: "Сигнал",
			dismiss: "Отхвърляне"
		},
		"cs-CZ": {
			alert: "Výstraha",
			dismiss: "Odstranit"
		},
		"da-DK": {
			alert: "Advarsel",
			dismiss: "Luk"
		},
		"de-DE": {
			alert: "Warnhinweis",
			dismiss: "Schließen"
		},
		"el-GR": {
			alert: "Ειδοποίηση",
			dismiss: "Απόρριψη"
		},
		"en-US": {
			dismiss: "Dismiss",
			alert: "Alert"
		},
		"es-ES": {
			alert: "Alerta",
			dismiss: "Descartar"
		},
		"et-EE": {
			alert: "Teade",
			dismiss: "Lõpeta"
		},
		"fi-FI": {
			alert: "Hälytys",
			dismiss: "Hylkää"
		},
		"fr-FR": {
			alert: "Alerte",
			dismiss: "Rejeter"
		},
		"he-IL": {
			alert: "התראה",
			dismiss: "התעלם"
		},
		"hr-HR": {
			alert: "Upozorenje",
			dismiss: "Odbaci"
		},
		"hu-HU": {
			alert: "Figyelmeztetés",
			dismiss: "Elutasítás"
		},
		"it-IT": {
			alert: "Avviso",
			dismiss: "Ignora"
		},
		"ja-JP": {
			alert: "アラート",
			dismiss: "閉じる"
		},
		"ko-KR": {
			alert: "경고",
			dismiss: "무시"
		},
		"lt-LT": {
			alert: "Įspėjimas",
			dismiss: "Atmesti"
		},
		"lv-LV": {
			alert: "Brīdinājums",
			dismiss: "Nerādīt"
		},
		"nb-NO": {
			alert: "Varsel",
			dismiss: "Lukk"
		},
		"nl-NL": {
			alert: "Melding",
			dismiss: "Negeren"
		},
		"pl-PL": {
			alert: "Ostrzeżenie",
			dismiss: "Zignoruj"
		},
		"pt-BR": {
			alert: "Alerta",
			dismiss: "Descartar"
		},
		"pt-PT": {
			alert: "Alerta",
			dismiss: "Dispensar"
		},
		"ro-RO": {
			alert: "Alertă",
			dismiss: "Revocare"
		},
		"ru-RU": {
			alert: "Предупреждение",
			dismiss: "Пропустить"
		},
		"sk-SK": {
			alert: "Upozornenie",
			dismiss: "Zrušiť"
		},
		"sl-SI": {
			alert: "Opozorilo",
			dismiss: "Opusti"
		},
		"sr-SP": {
			alert: "Upozorenje",
			dismiss: "Odbaci"
		},
		"sv-SE": {
			alert: "Varning",
			dismiss: "Avvisa"
		},
		"tr-TR": {
			alert: "Uyarı",
			dismiss: "Kapat"
		},
		"uk-UA": {
			alert: "Сигнал тривоги",
			dismiss: "Скасувати"
		},
		"zh-CN": {
			alert: "警报",
			dismiss: "取消"
		},
		"zh-TW": {
			alert: "警示",
			dismiss: "關閉"
		}
	};
	var Ni, Hi, Vi, qi, Wi, Gi, Ki, $i, Qi, Xi, Yi, Ji, Zi, ea, ta, ra, na, oa, ia, aa, ca, sa, ua, la, da, pa = {};
	ji(pa, "spectrum-Dialog", (() => Ni), (e => Ni = e)), ji(pa, "spectrum-Dialog--small", (() => Hi), (e => Hi = e)), ji(pa, "spectrum-Dialog--medium", (() => Vi), (e => Vi = e)), ji(pa, "spectrum-Dialog--large", (() => qi), (e => qi = e)), ji(pa, "spectrum-Dialog-hero", (() => Wi), (e => Wi = e)), ji(pa, "spectrum-Dialog-grid", (() => Gi), (e => Gi = e)), ji(pa, "spectrum-Dialog-heading", (() => Ki), (e => Ki = e)), ji(pa, "spectrum-Dialog-heading--noHeader", (() => $i), (e => $i = e)), ji(pa, "spectrum-Dialog-heading--noTypeIcon", (() => Qi), (e => Qi = e)), ji(pa, "spectrum-Dialog-header", (() => Xi), (e => Xi = e)), ji(pa, "spectrum-Dialog-header--noTypeIcon", (() => Yi), (e => Yi = e)), ji(pa, "spectrum-Dialog-typeIcon", (() => Ji), (e => Ji = e)), ji(pa, "spectrum-Dialog-divider", (() => Zi), (e => Zi = e)), ji(pa, "spectrum-Dialog--noDivider", (() => ea), (e => ea = e)), ji(pa, "spectrum-Dialog-content", (() => ta), (e => ta = e)), ji(pa, "spectrum-Dialog-footer", (() => ra), (e => ra = e)), ji(pa, "spectrum-Button", (() => na), (e => na = e)), ji(pa, "spectrum-Dialog-buttonGroup", (() => oa), (e => oa = e)), ji(pa, "spectrum-Dialog-buttonGroup--noFooter", (() => ia), (e => ia = e)), ji(pa, "spectrum-Dialog--dismissable", (() => aa), (e => aa = e)), ji(pa, "spectrum-Dialog-closeButton", (() => ca), (e => ca = e)), ji(pa, "spectrum-Dialog--error", (() => sa), (e => sa = e)), ji(pa, "spectrum-Dialog--fullscreen", (() => ua), (e => ua = e)), ji(pa, "spectrum-Dialog--fullscreenTakeover", (() => la), (e => la = e)), ji(pa, "spectrum-Dialog--warning", (() => da), (e => da = e)), Ni = "spectrum-Dialog_6d8b48", Hi = "spectrum-Dialog--small_6d8b48", Vi = "spectrum-Dialog--medium_6d8b48", qi = "spectrum-Dialog--large_6d8b48", Wi = "spectrum-Dialog-hero_6d8b48", Gi = "spectrum-Dialog-grid_6d8b48", Ki = "spectrum-Dialog-heading_6d8b48", $i = "spectrum-Dialog-heading--noHeader_6d8b48", Qi = "spectrum-Dialog-heading--noTypeIcon_6d8b48", Xi = "spectrum-Dialog-header_6d8b48", Yi = "spectrum-Dialog-header--noTypeIcon_6d8b48", Ji = "spectrum-Dialog-typeIcon_6d8b48", Zi = "spectrum-Dialog-divider_6d8b48", ea = "spectrum-Dialog--noDivider_6d8b48", ta = "spectrum-Dialog-content_6d8b48", ra = "spectrum-Dialog-footer_6d8b48", na = "spectrum-Button_6d8b48", oa = "spectrum-Dialog-buttonGroup_6d8b48", ia = "spectrum-Dialog-buttonGroup--noFooter_6d8b48", aa = "spectrum-Dialog--dismissable_6d8b48", ca = "spectrum-Dialog-closeButton_6d8b48", sa = "spectrum-Dialog--error_6d8b48", ua = "spectrum-Dialog--fullscreen_6d8b48", la = "spectrum-Dialog--fullscreenTakeover_6d8b48", da = "spectrum-Dialog--warning_6d8b48";
	let fa = {
		S: "small",
		M: "medium",
		L: "large",
		fullscreen: "fullscreen",
		fullscreenTakeover: "fullscreenTakeover"
	};

	function ma(e, t) {
		let {
			type: r = "modal",
			...n
		} = u.useContext(Li) || {}, {
			children: o,
			isDismissable: i = n.isDismissable,
			onDismiss: a = n.onClose,
			size: c,
			...s
		} = e, l = Lt(Fi(Ui)), {
			styleProps: d
		} = rr(s);
		c = "popover" === r ? c || "S" : c || "L";
		let p = qt(t),
			f = u.useRef(),
			m = fa[r] || fa[c],
			{
				dialogProps: g,
				titleProps: v
			} = Ei(ct(n, e), p),
			b = sr(`.${Fi(pa)["spectrum-Dialog-header"]}`, Gt(f)),
			y = sr(`.${Fi(pa)["spectrum-Dialog-heading"]}`, Gt(f)),
			_ = sr(`.${Fi(pa)["spectrum-Dialog-footer"]}`, Gt(f)),
			w = sr(`.${Fi(pa)["spectrum-Dialog-typeIcon"]}`, Gt(f)),
			k = u.useMemo((() => ({
				hero: {
					UNSAFE_className: Fi(pa)["spectrum-Dialog-hero"]
				},
				heading: {
					UNSAFE_className: Ht(Fi(pa), "spectrum-Dialog-heading", {
						"spectrum-Dialog-heading--noHeader": !b,
						"spectrum-Dialog-heading--noTypeIcon": !w
					}),
					level: 2,
					...v
				},
				header: {
					UNSAFE_className: Ht(Fi(pa), "spectrum-Dialog-header", {
						"spectrum-Dialog-header--noHeading": !y,
						"spectrum-Dialog-header--noTypeIcon": !w
					})
				},
				typeIcon: {
					UNSAFE_className: Fi(pa)["spectrum-Dialog-typeIcon"]
				},
				divider: {
					UNSAFE_className: Fi(pa)["spectrum-Dialog-divider"],
					size: "M"
				},
				content: {
					UNSAFE_className: Fi(pa)["spectrum-Dialog-content"]
				},
				footer: {
					UNSAFE_className: Fi(pa)["spectrum-Dialog-footer"]
				},
				buttonGroup: {
					UNSAFE_className: Ht(Fi(pa), "spectrum-Dialog-buttonGroup", {
						"spectrum-Dialog-buttonGroup--noFooter": !_
					}),
					align: "end"
				}
			})), [_, b, v]);
		return h.createElement("section", {
			...d,
			...g,
			className: Ht(Fi(pa), "spectrum-Dialog", {
				[`spectrum-Dialog--${m}`]: m,
				"spectrum-Dialog--dismissable": i
			}, d.className),
			ref: p
		}, h.createElement(hi, {
			ref: f,
			UNSAFE_className: Fi(pa)["spectrum-Dialog-grid"]
		}, h.createElement(cr, {
			slots: k
		}, o), i && h.createElement(li, {
			UNSAFE_className: Fi(pa)["spectrum-Dialog-closeButton"],
			isQuiet: !0,
			"aria-label": l.format("dismiss"),
			onPress: a
		}, h.createElement(Bi, null))))
	}
	let ga = h.forwardRef(ma);
	const ha = 4;
	const va = "@assets/selectors/DestinationSelector",
		ba = {
			discoveryURL: {
				type: De.string
			},
			imsOrg: {
				type: De.string,
				defaultValue: ze
			},
			imsToken: {
				type: De.string
			},
			apiKey: {
				type: De.string,
				defaultValue: Me
			},
			rootPath: {
				type: De.string
			},
			path: {
				type: De.string
			},
			initRepoId: {
				type: De.string
			},
			onCreateFolder: {
				type: De.func
			},
			handleSelection: {
				type: De.func
			},
			onConfirm: {
				type: De.func
			},
			confirmDisabled: {
				type: De.bool
			},
			viewType: {
				type: De.string
			},
			viewTypeOptions: {
				type: De.array
			},
			i18nSymbols: {
				type: De.objectOf(De.shape({
					id: De.string,
					defaultMessage: De.string,
					description: De.string
				}))
			},
			inlineAlertSetup: {
				type: De.objectOf(De.shape({
					header: De.string,
					message: De.string,
					height: De.number
				}))
			},
			acvConfig: {
				type: De.object
			},
			intl: {
				type: De.object
			},
			onClose: {
				type: De.func
			},
			noWrap: {
				type: De.bool,
				defaultValue: !1
			},
			dialogSize: {
				type: De.string,
				defaultValue: "fullscreen"
			},
			colorScheme: {
				type: De.string
			},
			env: {
				type: De.string,
				defaultValue: Fe
			},
			version: {
				type: De.string
			},
			statusScreenProps: {
				type: De.object
			},
			waitForImsToken: {
				type: De.bool,
				defaultValue: !1
			},
			infoPopoverMap: {
				type: De.func
			}
		};
	S.PropsUtils.getPropTypes(ba), S.PropsUtils.getDefaultProps(ba);
	const ya = e => {
			const t = S.PropsUtils.useResolvedProps(e, ba, va),
				{
					cssWidth: r,
					cssHeight: n,
					handleSizeUpdate: o
				} = function(e) {
					const [t, r] = u.useState(`${e.width}px`), [n, o] = u.useState(`${e.height}px`);
					return {
						cssWidth: t,
						cssHeight: n,
						handleSizeUpdate: function(e) {
							e.height && o(`${e.height+ha}px`), e.width && r(`${e.width+ha}px`)
						}
					}
				}({
					width: 640,
					height: 700
				}),
				i = S.useShim({
					frontend: "DestinationSelector",
					serviceId: va,
					solutionName: "CQ-assets-selectors",
					env: t.env,
					version: t.version
				}),
				a = je(t, null == t ? void 0 : t.env);
			return h.createElement(h.Fragment, null, h.createElement(ga, {
				UNSAFE_style: Oe,
				width: r,
				height: n,
				role: "dialog",
				"data-testid": "destination-selector-shim-dialog"
			}, h.createElement("div", {
				style: Ae,
				"data-testid": "destination-selector-shim-container"
			}, h.createElement(i, {
				...a,
				onSizeUpdate: o
			}))))
		},
		_a = e => {
			const t = {
					heading: "Waiting for user to authenticate...",
					description: " ",
					scale: 1
				},
				[r, n] = h.useState(null);
			return u.useEffect((() => {
				e.imsToken ? n(null) : n(t)
			}), [e.imsToken]), h.createElement(ya, {
				...e,
				waitForImsToken: !0,
				statusScreenProps: r
			})
		};
	var wa = {},
		ka = {},
		xa = {},
		Ta = {};
	Object.defineProperty(Ta, "__esModule", {
		value: !0
	});
	var Sa = function() {
		function e() {
			this.data = {}, this.length = 0
		}
		return e.prototype.clear = function() {
			this.data = {}, this.length = 0
		}, e.prototype.getItem = function(e) {
			var t = this.data[e];
			return t || null
		}, e.prototype.removeItem = function(e) {
			return !!this.data[e] && (delete this.data[e], --this.length, !0)
		}, e.prototype.setItem = function(e, t) {
			this.data[e] || ++this.length, this.data[e] = t
		}, e.prototype.key = function(e) {
			throw new Error("Method not implemented. " + e)
		}, e
	}();
	Ta.MemoryStorage = Sa;
	var Ia = {};
	! function(e) {
		var t;
		Object.defineProperty(e, "__esModule", {
			value: !0
		}), e.AdobeIdKey = "adobeid", e.AdobeIMSKey = "adobeIMS", e.AdobeImsFactory = "adobeImsFactory", e.DEFAULT_LANGUAGE = "en_US", (t = e.STORAGE_MODE || (e.STORAGE_MODE = {})).LocalStorage = "local", t.SessionStorage = "session", t.MemoryStorage = "memory", e.HEADERS = {
			AUTHORIZATION: "Authorization",
			X_IMS_CLIENT_ID: "X-IMS-ClientId",
			RETRY_AFTER: "Retry-after"
		}, e.PROFILE_STORAGE_KEY = "adobeid_ims_profile", e.TOKEN_STORAGE_KEY = "adobeid_ims_access_token", e.ON_IMSLIB_INSTANCE = "onImsLibInstance", e.ASK_FOR_IMSLIB_INSTANCE_DOM_EVENT_NAME = "getImsLibInstance"
	}(Ia);
	var Ea = {},
		Pa = t && t.__read || function(e, t) {
			var r = "function" == typeof Symbol && e[Symbol.iterator];
			if (!r) return e;
			var n, o, i = r.call(e),
				a = [];
			try {
				for (;
					(void 0 === t || t-- > 0) && !(n = i.next()).done;) a.push(n.value)
			} catch (e) {
				o = {
					error: e
				}
			} finally {
				try {
					n && !n.done && (r = i.return) && r.call(i)
				} finally {
					if (o) throw o.error
				}
			}
			return a
		},
		Da = t && t.__spread || function() {
			for (var e = [], t = 0; t < arguments.length; t++) e = e.concat(Pa(arguments[t]));
			return e
		};
	Object.defineProperty(Ea, "__esModule", {
		value: !0
	});
	var Ca = function() {
		function e() {
			var e = this;
			this.logEnabled = !1, this.print = function(t, r) {
				e.logEnabled && t.apply(void 0, Da(r))
			}, this.assert = function(t, r) {
				e.print(console.assert, [t, r])
			}, this.assertCondition = function(t, r) {
				t() || e.print(console.error, [r])
			}, this.error = function() {
				for (var t = [], r = 0; r < arguments.length; r++) t[r] = arguments[r];
				e.print(console.error, t)
			}, this.warn = function() {
				for (var t = [], r = 0; r < arguments.length; r++) t[r] = arguments[r];
				e.print(console.warn, t)
			}, this.info = function() {
				for (var t = [], r = 0; r < arguments.length; r++) t[r] = arguments[r];
				e.print(console.info, t)
			}
		}
		return e.prototype.enableLogging = function() {
			this.logEnabled = !0
		}, e.prototype.disableLogging = function() {
			this.logEnabled = !1
		}, e
	}();
	Ea.default = new Ca;
	var Aa = t && t.__importDefault || function(e) {
		return e && e.__esModule ? e : {
			default: e
		}
	};
	Object.defineProperty(xa, "__esModule", {
		value: !0
	});
	var Oa = Ta,
		Ba = Ia,
		Ra = Aa(Ea),
		Ma = function() {
			function e() {
				this.memoryStorageInstance = null
			}
			return Object.defineProperty(e.prototype, "memoryStorage", {
				get: function() {
					return this.memoryStorageInstance || (this.memoryStorageInstance = new Oa.MemoryStorage), this.memoryStorageInstance
				},
				enumerable: !0,
				configurable: !0
			}), e.prototype.getStorageByName = function(e) {
				var t = this.getStorageInstanceByName(e);
				return t && this.verifyStorage(t) ? t : this.memoryStorage
			}, e.prototype.getStorageInstanceByName = function(e) {
				if (e === Ba.STORAGE_MODE.MemoryStorage) return this.memoryStorage;
				try {
					return e === Ba.STORAGE_MODE.LocalStorage ? window.localStorage : window.sessionStorage
				} catch (e) {
					return Ra.default.warn("Please change your cookies settings in order to allow local data to be set"), null
				}
			}, e.prototype.getAvailableStorage = function() {
				var e = this.getStorageByName(Ba.STORAGE_MODE.LocalStorage);
				return e instanceof Oa.MemoryStorage ? this.getStorageByName(Ba.STORAGE_MODE.SessionStorage) : e
			}, e.prototype.verifyStorage = function(e) {
				var t = "test";
				try {
					return e.setItem(t, "true"), "true" === e.getItem(t) && (e.removeItem(t), !0)
				} catch (e) {
					return !1
				}
			}, e
		}();
	xa.default = new Ma,
		function(e) {
			var r = t && t.__importDefault || function(e) {
				return e && e.__esModule ? e : {
					default: e
				}
			};
			Object.defineProperty(e, "__esModule", {
				value: !0
			});
			var n = r(xa),
				o = Ta,
				i = "nonce";
			e.ONE_HOUR = 1296e4;
			var a = function() {
				function t(e) {
					this.storageInstance = null, this.nonceStorageKey = "", this.nonceStorageKey = "" + i + e
				}
				return Object.defineProperty(t.prototype, "storage", {
					get: function() {
						return this.storageInstance || (this.storageInstance = n.default.getAvailableStorage()), this.storageInstance
					},
					enumerable: !0,
					configurable: !0
				}), t.prototype.initialize = function() {
					if (!this.isStorageAvailable()) return "";
					var e = t.generateNonce(),
						r = this.getNonceFromStorage() || {};
					return r = this.clearOlderNonceKeys(r), this.addNonceToObject(r, e), this.saveNonceValuesToStorage(r), e.value
				}, t.prototype.addNonceToObject = function(e, t) {
					e[t.value] = t.expiry
				}, t.prototype.clearOlderNonceKeys = function(t, r) {
					void 0 === r && (r = e.ONE_HOUR);
					var n = Date.now() - r;
					return Object.keys(t).forEach((function(e) {
						parseInt(t[e]) < n && delete t[e]
					})), t
				}, t.prototype.verify = function(e) {
					if (!this.isStorageAvailable()) return !0;
					var t = this.getNonceFromStorage();
					if (!t) return !1;
					var r = this.clearOlderNonceKeys(t),
						n = null !== (r[e] || null);
					return n && this.clearNonceValueFromStorage(r, e), n
				}, t.prototype.clearNonceValueFromStorage = function(e, t) {
					delete e[t], this.saveNonceValuesToStorage(e)
				}, t.prototype.getNonceFromStorage = function() {
					var e = this.storage.getItem(this.nonceStorageKey);
					return e ? JSON.parse(e) : null
				}, t.prototype.saveNonceValuesToStorage = function(e) {
					this.storage.setItem(this.nonceStorageKey, JSON.stringify(e))
				}, t.prototype.isStorageAvailable = function() {
					return !(this.storage instanceof o.MemoryStorage)
				}, t.cryptoRndomString = function() {
					if (!window.crypto) return "";
					var e = new Uint32Array(3);
					return window.crypto.getRandomValues(e), e.join("").substr(0, 16)
				}, t.randomString = function() {
					var e = t.cryptoRndomString();
					if (e) return e;
					for (var r = "", n = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", o = 0; o < 16; o++) r += n.charAt(Math.floor(Math.random() * n.length));
					return r
				}, t.generateNonce = function() {
					return {
						value: t.randomString(),
						expiry: (new Date).getTime().toString()
					}
				}, t
			}();
			e.CsrfService = a
		}(ka);
	var za = {},
		Fa = {},
		ja = t && t.__awaiter || function(e, t, r, n) {
			return new(r || (r = Promise))((function(o, i) {
				function a(e) {
					try {
						s(n.next(e))
					} catch (e) {
						i(e)
					}
				}

				function c(e) {
					try {
						s(n.throw(e))
					} catch (e) {
						i(e)
					}
				}

				function s(e) {
					var t;
					e.done ? o(e.value) : (t = e.value, t instanceof r ? t : new r((function(e) {
						e(t)
					}))).then(a, c)
				}
				s((n = n.apply(e, t || [])).next())
			}))
		},
		La = t && t.__generator || function(e, t) {
			var r, n, o, i, a = {
				label: 0,
				sent: function() {
					if (1 & o[0]) throw o[1];
					return o[1]
				},
				trys: [],
				ops: []
			};
			return i = {
				next: c(0),
				throw: c(1),
				return: c(2)
			}, "function" == typeof Symbol && (i[Symbol.iterator] = function() {
				return this
			}), i;

			function c(i) {
				return function(c) {
					return function(i) {
						if (r) throw new TypeError("Generator is already executing.");
						for (; a;) try {
							if (r = 1, n && (o = 2 & i[0] ? n.return : i[0] ? n.throw || ((o = n.return) && o.call(n), 0) : n.next) && !(o = o.call(n, i[1])).done) return o;
							switch (n = 0, o && (i = [2 & i[0], o.value]), i[0]) {
								case 0:
								case 1:
									o = i;
									break;
								case 4:
									return a.label++, {
										value: i[1],
										done: !1
									};
								case 5:
									a.label++, n = i[1], i = [0];
									continue;
								case 7:
									i = a.ops.pop(), a.trys.pop();
									continue;
								default:
									if (!(o = a.trys, (o = o.length > 0 && o[o.length - 1]) || 6 !== i[0] && 2 !== i[0])) {
										a = 0;
										continue
									}
									if (3 === i[0] && (!o || i[1] > o[0] && i[1] < o[3])) {
										a.label = i[1];
										break
									}
									if (6 === i[0] && a.label < o[1]) {
										a.label = o[1], o = i;
										break
									}
									if (o && a.label < o[2]) {
										a.label = o[2], a.ops.push(i);
										break
									}
									o[2] && a.ops.pop(), a.trys.pop();
									continue
							}
							i = t.call(e, a)
						} catch (e) {
							i = [6, e], n = 0
						} finally {
							r = o = 0
						}
						if (5 & i[0]) throw i[1];
						return {
							value: i[0] ? i[1] : void 0,
							done: !0
						}
					}([i, c])
				}
			}
		};
	Object.defineProperty(Fa, "__esModule", {
		value: !0
	});
	var Ua = function() {
		function e() {}
		return e.prototype.uriEncodeData = function(e) {
			if ("object" != typeof e) return "";
			var t, r = [],
				n = "";
			for (var o in e) void 0 !== (t = e[o]) && (n = this.encodeValue(t), r.push(encodeURIComponent(o) + "=" + n));
			return r.join("&")
		}, e.prototype.encodeValue = function(e) {
			return null === e ? "null" : "object" == typeof e ? encodeURIComponent(JSON.stringify(e)) : encodeURIComponent(e)
		}, e.prototype.replaceUrl = function(e) {
			e && window.location.replace(e)
		}, e.prototype.sleep = function(e) {
			return ja(this, void 0, void 0, (function() {
				return La(this, (function(t) {
					return [2, new Promise((function(t) {
						return setTimeout(t, e)
					}))]
				}))
			}))
		}, e.prototype.replaceUrlAndWait = function(e, t) {
			return ja(this, void 0, void 0, (function() {
				return La(this, (function(r) {
					switch (r.label) {
						case 0:
							return e ? (window.location.replace(e), [4, this.sleep(t)]) : [2, Promise.resolve()];
						case 1:
							return r.sent(), [2, Promise.resolve()]
					}
				}))
			}))
		}, e.prototype.setHrefUrl = function(e) {
			e && (window.location.href = e)
		}, e.prototype.setHash = function(e) {
			void 0 === e && (e = ""), window.location.hash = e
		}, e
	}();
	Fa.default = new Ua;
	var Na = {},
		Ha = {};
	! function(e) {
		Object.defineProperty(e, "__esModule", {
				value: !0
			}),
			function(e) {
				e.STAGE = "stg1", e.PROD = "prod"
			}(e.IEnvironment || (e.IEnvironment = {}))
	}(Ha);
	var Va = {},
		qa = t && t.__values || function(e) {
			var t = "function" == typeof Symbol && Symbol.iterator,
				r = t && e[t],
				n = 0;
			if (r) return r.call(e);
			if (e && "number" == typeof e.length) return {
				next: function() {
					return e && n >= e.length && (e = void 0), {
						value: e && e[n++],
						done: !e
					}
				}
			};
			throw new TypeError(t ? "Object is not iterable." : "Symbol.iterator is not defined.")
		};
	Object.defineProperty(Va, "__esModule", {
		value: !0
	});
	var Wa = function() {
		function e(e, t, r) {
			void 0 === e && (e = !1), void 0 === t && (t = ""), void 0 === r && (r = ""), this.proxied = e, this.url = t, this.fallbackUrl = r
		}
		return e.computeEndpoint = function(t, r, n, o) {
			var i, a;
			if (t) {
				var c = n ? e.THIRD_PARTY_DOMAINS_STAGE : e.THIRD_PARTY_DOMAINS_PROD;
				try {
					for (var s = qa(Object.keys(c)), u = s.next(); !u.done; u = s.next()) {
						var l = u.value;
						if (r === l || r.endsWith("." + l)) return new e(!0, c[l], o)
					}
				} catch (e) {
					i = {
						error: e
					}
				} finally {
					try {
						u && !u.done && (a = s.return) && a.call(s)
					} finally {
						if (i) throw i.error
					}
				}
			}
			return new e(!1, o)
		}, e.prototype.shouldFallbackToAdobe = function(e) {
			return !!this.proxied && ("feature_disabled" === e.error && "cdsc" === e.error_description)
		}, e.THIRD_PARTY_DOMAINS_PROD = {
			"behance.net": "https://sso.behance.net"
		}, e.THIRD_PARTY_DOMAINS_STAGE = {
			"s2stagehance.com": "https://sso.s2stagehance.com"
		}, e
	}();
	Va.CheckTokenEndpoint = Wa, Object.defineProperty(Na, "__esModule", {
		value: !0
	});
	var Ga = Ha,
		Ka = Va,
		$a = function() {
			function e() {
				this.baseUrlAdobe = "", this.baseUrlServices = "", this.checkTokenEndpoint = new Ka.CheckTokenEndpoint, this.jslibver = "v2-v0.31.0-2-g1e8a8a8"
			}
			return e.prototype.loadEnvironment = function(e, t, r) {
				void 0 === t && (t = !1), void 0 === r && (r = "");
				var n = e === Ga.IEnvironment.STAGE;
				n ? (this.baseUrlAdobe = "https://ims-na1-stg1.adobelogin.com", this.baseUrlServices = "https://adobeid-na1-stg1.services.adobe.com") : (this.baseUrlAdobe = "https://ims-na1.adobelogin.com", this.baseUrlServices = "https://adobeid-na1.services.adobe.com"), this.checkTokenEndpoint = Ka.CheckTokenEndpoint.computeEndpoint(t, r, n, this.baseUrlServices)
			}, e
		}();
	Na.default = new $a;
	var Qa = {},
		Xa = {},
		Ya = {},
		Ja = {};

	function Za(e) {
		return null != e && "object" == typeof e && !Array.isArray(e)
	}
	Object.defineProperty(Ja, "__esModule", {
		value: !0
	}), Ja.isObject = Za, Ja.merge = function e(t, r) {
		if (null == t) return r;
		if (t === r) return t;
		if (!Za(t)) return t;
		var n = Object.assign({}, t);
		return Za(r) && Object.keys(r).forEach((function(o) {
			var i, a;
			Za(r[o]) ? o in t ? n[o] = e(t[o], r[o]) : Object.assign(n, ((i = {})[o] = r[o], i)) : Object.assign(n, ((a = {})[o] = r[o], a))
		})), n
	}, Object.defineProperty(Ya, "__esModule", {
		value: !0
	});
	var ec = Ja,
		tc = function() {
			function e() {
				this.getCustomApiParameters = function(e, t) {
					return e[t] || {}
				}
			}
			return e.prototype.mergeExternalParameters = function(e, t, r) {
				return ec.merge(this.getCustomApiParameters(t, r), e)
			}, e.prototype.toJson = function(e) {
				try {
					return "string" != typeof e ? e : JSON.parse(e)
				} catch (e) {
					return null
				}
			}, e
		}();
	Ya.default = new tc;
	var rc = t && t.__importDefault || function(e) {
		return e && e.__esModule ? e : {
			default: e
		}
	};
	Object.defineProperty(Xa, "__esModule", {
		value: !0
	});
	var nc = rc(Ya),
		oc = Ja,
		ic = function() {
			function e() {}
			return e.getInitialRedirectUri = function(e, t) {
				var r = e.redirect_uri || t || window.location.href,
					n = "function" == typeof r ? r() : r,
					o = n.indexOf("from_ims");
				return -1 === o ? n : ("#" === n[o - 1] && o--, n.substr(0, o))
			}, e.createDefaultRedirectUrl = function(e, t, r, n) {
				var o = this.getInitialRedirectUri(r, e),
					i = this.createOldHash(o);
				return i.indexOf("?") > 0 ? i + "&client_id=" + t + "&api=" + n : i + "?client_id=" + t + "&api=" + n
			}, e.createRedirectUrl = function(e, t, r, n, o) {
				void 0 === o && (o = "");
				var i = this.createDefaultRedirectUrl(e, t, r, n);
				(o = o || r.scope || "") && (i = i + "&scope=" + o);
				var a = r.reauth || "";
				return a && (i = i + "&reauth=" + a), i
			}, e.createOldHash = function(e) {
				var t = e.indexOf("#");
				return t < 0 ? e + "#old_hash=&from_ims=true" : e.substring(0, t) + "#old_hash=" + e.substring(t + 1) + "&from_ims=true"
			}, e.mergeApiParamsWithExternalParams = function(e, t, r) {
				return oc.merge(nc.default.getCustomApiParameters(e, r), t)
			}, e
		}();
	Xa.RedirectHelper = ic;
	var ac = t && t.__assign || function() {
			return ac = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, ac.apply(this, arguments)
		},
		cc = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(Qa, "__esModule", {
		value: !0
	});
	var sc = cc(Fa),
		uc = cc(Na),
		lc = Xa,
		dc = Ja,
		pc = function() {
			var e = this;
			this.composeRedirectUrl = function(e) {
				var t = "authorize",
					r = e.apiParameters,
					n = e.externalParameters,
					o = void 0 === n ? {} : n,
					i = e.adobeIdRedirectUri,
					a = void 0 === i ? "" : i,
					c = e.clientId,
					s = e.locale,
					u = e.state,
					l = void 0 === u ? {} : u,
					d = e.scope,
					p = void 0 === d ? o.scope || r.scope || "" : d,
					f = lc.RedirectHelper.mergeApiParamsWithExternalParams(r, o, t);
				l && (f.state = dc.merge(f.state || {}, l));
				var m = lc.RedirectHelper.createRedirectUrl(a, c, f, t, p),
					g = o.locale || s || "",
					h = e.response_type,
					v = void 0 === h ? f.response_type || "" : h;
				return ac(ac({}, f), {
					client_id: c,
					scope: p,
					locale: g,
					response_type: v,
					jslVersion: uc.default.jslibver,
					redirect_uri: m
				})
			}, this.createRedirectUrl = function(t) {
				var r = e.composeRedirectUrl(t),
					n = sc.default.uriEncodeData(r);
				return uc.default.baseUrlAdobe + "/ims/authorize/v1?" + n
			}
		};
	Qa.BaseSignInService = pc;
	var fc = t && t.__extends || function() {
			var e = function(t, r) {
				return e = Object.setPrototypeOf || {
					__proto__: []
				}
				instanceof Array && function(e, t) {
					e.__proto__ = t
				} || function(e, t) {
					for (var r in t) t.hasOwnProperty(r) && (e[r] = t[r])
				}, e(t, r)
			};
			return function(t, r) {
				function n() {
					this.constructor = t
				}
				e(t, r), t.prototype = null === r ? Object.create(r) : (n.prototype = r.prototype, new n)
			}
		}(),
		mc = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(za, "__esModule", {
		value: !0
	});
	var gc = mc(Fa),
		hc = mc(Na),
		vc = function(e) {
			function t() {
				var t = null !== e && e.apply(this, arguments) || this;
				return t.signIn = function(e) {
					var r = t.createRedirectUrl(e);
					gc.default.setHrefUrl(r)
				}, t.authorizeToken = function(e, r) {
					var n = t.composeRedirectUrl(r);
					e && (n.user_assertion = e, n.user_assertion_type = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"), t.createAuthorizeForm(n).submit()
				}, t
			}
			return fc(t, e), t.prototype.createAuthorizeForm = function(e) {
				var t = hc.default.baseUrlAdobe + "/ims/authorize/v1",
					r = document.createElement("form");
				r.style.display = "none", r.setAttribute("method", "post"), r.setAttribute("action", t);
				var n = null,
					o = null,
					i = "";
				for (var a in e) {
					if ("object" == typeof(o = e[a])) {
						if (0 === Object.keys(o).length) continue;
						i = JSON.stringify(o)
					} else i = o;
					"" !== i && (n = this.createFormElement("input", "text", a, i), r.appendChild(n))
				}
				return document.getElementsByTagName("body")[0].appendChild(r), r
			}, t.prototype.createFormElement = function(e, t, r, n) {
				var o = document.createElement(e);
				return o.setAttribute("type", t), o.setAttribute("name", r), o.setAttribute("value", n), o
			}, t
		}(Qa.BaseSignInService);
	za.SignInService = vc;
	var bc = {},
		yc = {},
		_c = t && t.__read || function(e, t) {
			var r = "function" == typeof Symbol && e[Symbol.iterator];
			if (!r) return e;
			var n, o, i = r.call(e),
				a = [];
			try {
				for (;
					(void 0 === t || t-- > 0) && !(n = i.next()).done;) a.push(n.value)
			} catch (e) {
				o = {
					error: e
				}
			} finally {
				try {
					n && !n.done && (r = i.return) && r.call(i)
				} finally {
					if (o) throw o.error
				}
			}
			return a
		},
		wc = t && t.__spread || function() {
			for (var e = [], t = 0; t < arguments.length; t++) e = e.concat(_c(arguments[t]));
			return e
		};
	Object.defineProperty(yc, "__esModule", {
		value: !0
	});
	var kc = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/,
		xc = ["https://auth.services.adobe.com", "https://auth-stg1.services.adobe.com", "https://localhost.corp.adobe.com:9000"],
		Tc = function() {
			var e = this;
			this.windowObjectReference = null, this.previousUrl = "", this.openSignInWindow = function(t, r, n, o) {
				e.onProcessLocation = o, e.allowOrigin = n.allowOrigin, e.timerId && clearInterval(e.timerId), window.removeEventListener("message", e.receiveMessage), window.addEventListener("message", e.receiveMessage);
				var i = "toolbar=no, menubar=no, width=" + n.width + ", height=" + n.height + ", top=" + n.top + ", left=" + n.left;
				!e.windowObjectReference || e.windowObjectReference && e.windowObjectReference.closed ? e.windowObjectReference = window.open(t, n.title, i) : e.previousUrl !== t ? (e.windowObjectReference = window.open(t, n.title, i), e.windowObjectReference && e.windowObjectReference.focus()) : e.windowObjectReference.focus();
				var a = e.windowObjectReference || {};
				a.opener || (e.timerId = setInterval((function() {
					a[r] && (clearInterval(e.timerId), e.onProcessLocation && e.onProcessLocation(a[r]), delete a[r], e.windowObjectReference && e.windowObjectReference.close())
				}), 500)), e.previousUrl = t
			}, this.receiveMessage = function(t) {
				if (wc(xc, [e.allowOrigin]).includes(t.origin)) {
					try {
						if (!kc.test(t.data)) return
					} catch (e) {
						return
					}
					e.onProcessLocation && e.onProcessLocation(t.data)
				}
			}
		};
	yc.default = new Tc;
	var Sc = t && t.__extends || function() {
			var e = function(t, r) {
				return e = Object.setPrototypeOf || {
					__proto__: []
				}
				instanceof Array && function(e, t) {
					e.__proto__ = t
				} || function(e, t) {
					for (var r in t) t.hasOwnProperty(r) && (e[r] = t[r])
				}, e(t, r)
			};
			return function(t, r) {
				function n() {
					this.constructor = t
				}
				e(t, r), t.prototype = null === r ? Object.create(r) : (n.prototype = r.prototype, new n)
			}
		}(),
		Ic = t && t.__assign || function() {
			return Ic = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, Ic.apply(this, arguments)
		},
		Ec = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(bc, "__esModule", {
		value: !0
	});
	var Pc = Ec(yc),
		Dc = function(e) {
			function t(t, r) {
				var n = e.call(this) || this;
				return n.signIn = function(e) {
					e.state = Ic(Ic({}, e.state), {
						imslibmodal: !0
					});
					var t = e.state.nonce,
						r = n.createRedirectUrl(e);
					Pc.default.openSignInWindow(r, t, n.popupSettings, n.onPopupMessage)
				}, n.onPopupMessage = t, n.popupSettings = r, n
			}
			return Sc(t, e), t
		}(Qa.BaseSignInService);
	bc.SignInModalService = Dc;
	var Cc = {},
		Ac = t && t.__assign || function() {
			return Ac = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, Ac.apply(this, arguments)
		},
		Oc = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(Cc, "__esModule", {
		value: !0
	});
	var Bc = Xa,
		Rc = Oc(Fa),
		Mc = Oc(Na),
		zc = function() {
			this.signOut = function(e) {
				var t = "logout",
					r = e.apiParameters,
					n = e.externalParameters,
					o = e.adobeIdRedirectUri,
					i = void 0 === o ? "" : o,
					a = e.clientId,
					c = Bc.RedirectHelper.mergeApiParamsWithExternalParams(r, n, t),
					s = Bc.RedirectHelper.createDefaultRedirectUrl(i, a, c, t),
					u = Ac(Ac({}, c), {
						client_id: a,
						redirect_uri: s,
						jslVersion: Mc.default.jslibver
					}),
					l = Rc.default.uriEncodeData(u),
					d = Mc.default.baseUrlAdobe + "/ims/logout/v1?" + l;
				Rc.default.replaceUrl(d)
			}
		};
	Cc.SignOutService = zc;
	var Fc = {},
		jc = {};
	Object.defineProperty(jc, "__esModule", {
		value: !0
	});
	var Lc = function(e) {
		void 0 === e && (e = {}), this.title = "Adobe ID", this.width = 600, this.top = 100, this.left = 100;
		var t = e.title,
			r = void 0 === t ? "Adobe ID" : t,
			n = e.width,
			o = void 0 === n ? 600 : n,
			i = e.height,
			a = void 0 === i ? 700 : i,
			c = e.top,
			s = void 0 === c ? 100 : c,
			u = e.left,
			l = void 0 === u ? 100 : u,
			d = e.allowedOrigin;
		this.title = r, this.width = o, this.height = a, this.top = s, this.left = l, this.allowOrigin = d
	};
	jc.PopupSettings = Lc;
	var Uc = {};
	Object.defineProperty(Uc, "__esModule", {
		value: !0
	});
	var Nc = function(e) {
		this.token = "", this.sid = "", this.expirems = 0;
		var t = e.token,
			r = e.expirems;
		this.token = t, this.expirems = r
	};
	Uc.StandaloneToken = Nc;
	var Hc = {};
	! function(e) {
		Object.defineProperty(e, "__esModule", {
				value: !0
			}),
			function(e) {
				e.force = "force", e.check = "check"
			}(e.IReauth || (e.IReauth = {}))
	}(Hc);
	var Vc = {};
	! function(e) {
		Object.defineProperty(e, "__esModule", {
				value: !0
			}),
			function(e) {
				e.token = "token", e.code = "code"
			}(e.IGrantTypes || (e.IGrantTypes = {}))
	}(Vc);
	var qc = {},
		Wc = {};
	Object.defineProperty(Wc, "__esModule", {
		value: !0
	});
	var Gc = function() {
		this.appCode = "", this.appVersion = ""
	};
	Wc.AnalyticsParameters = Gc;
	var Kc = {};
	! function(e) {
		var r = t && t.__read || function(e, t) {
				var r = "function" == typeof Symbol && e[Symbol.iterator];
				if (!r) return e;
				var n, o, i = r.call(e),
					a = [];
				try {
					for (;
						(void 0 === t || t-- > 0) && !(n = i.next()).done;) a.push(n.value)
				} catch (e) {
					o = {
						error: e
					}
				} finally {
					try {
						n && !n.done && (r = i.return) && r.call(i)
					} finally {
						if (o) throw o.error
					}
				}
				return a
			},
			n = t && t.__spread || function() {
				for (var e = [], t = 0; t < arguments.length; t++) e = e.concat(r(arguments[t]));
				return e
			},
			o = t && t.__importDefault || function(e) {
				return e && e.__esModule ? e : {
					default: e
				}
			};
		Object.defineProperty(e, "__esModule", {
			value: !0
		});
		var i = o(xa);
		e.ONE_HOUR = 1296e4;
		var a = function() {
			function t() {
				this.storage = i.default.getAvailableStorage()
			}
			return t.prototype.b64Uri = function(e) {
				return btoa(e).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "")
			}, t.prototype.createCodeChallenge = function(e, t) {
				var r = this;
				void 0 === t && (t = 43);
				for (var o = window.msCrypto || window.crypto, i = this.b64Uri(Array.prototype.map.call(o.getRandomValues(new Uint8Array(t)), (function(e) {
						return String.fromCharCode(e)
					})).join("")).substring(0, t), a = new Uint8Array(i.length), c = 0; c < i.length; c++) a[c] = i.charCodeAt(c);
				var s = o.subtle.digest("SHA-256", a);
				return new Promise((function(t, o) {
					window.CryptoOperation ? (s.onerror = function(e) {
						return o(e)
					}, s.oncomplete = function(o) {
						var a = new Uint8Array(o.target.result),
							c = r.b64Uri(String.fromCharCode.apply(String, n(a)));
						return t(r.saveVerifierAndReturn(e, {
							verifier: i,
							challenge: c
						}))
					}) : s.then((function(o) {
						var a = new Uint8Array(o),
							c = r.b64Uri(String.fromCharCode.apply(String, n(a)));
						return t(r.saveVerifierAndReturn(e, {
							verifier: i,
							challenge: c
						}))
					}))
				}))
			}, t.prototype.saveVerifierAndReturn = function(e, t) {
				var r = this.getVerifierValuesFromStorage(),
					n = {
						verifier: t.verifier || "",
						expiry: (new Date).getTime().toString()
					};
				return r[e] = n, this.storage.setItem("verifiers", JSON.stringify(r)), Promise.resolve(t)
			}, t.prototype.getVerifierValuesFromStorage = function() {
				var e = this.storage.getItem("verifiers"),
					t = e ? JSON.parse(e) : {};
				return this.clearOlderVerifiers(t)
			}, t.prototype.clearOlderVerifiers = function(t, r) {
				void 0 === r && (r = e.ONE_HOUR);
				var n = Date.now() - r;
				return Object.keys(t).forEach((function(e) {
					parseInt(t[e]) < n && delete t[e]
				})), t
			}, t.prototype.getVerifierByKey = function(e) {
				var t = this.getVerifierValuesFromStorage(),
					r = t ? t[e] : {};
				return delete t[e], this.storage.setItem("verifiers", JSON.stringify(t)), r ? r.verifier : ""
			}, t
		}();
		e.CodeChallenge = a
	}(Kc);
	var $c = t && t.__assign || function() {
			return $c = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, $c.apply(this, arguments)
		},
		Qc = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(qc, "__esModule", {
		value: !0
	});
	var Xc = Ia,
		Yc = Ha,
		Jc = Qc(Na),
		Zc = Wc,
		es = Qc(Ea),
		ts = Hc,
		rs = Vc,
		ns = Kc,
		os = function() {
			function e(e) {
				void 0 === e && (e = null), this.analyticsParameters = new Zc.AnalyticsParameters, this.api_parameters = {}, this.locale = "", this.scope = "AdobeID", this.client_id = "", this.environment = Yc.IEnvironment.PROD, this.useLocalStorage = !1, this.onReady = null, this.onModalModeSignInComplete = null, this.proxiedCheckToken = !1;
				var t = e || window[Xc.AdobeIdKey];
				if (!t || !t.client_id) throw new Error("Please provide required adobeId, client_id information");
				var r = t.api_parameters,
					n = t.client_id,
					o = t.locale,
					i = t.scope,
					a = t.ijt,
					c = t.environment,
					s = void 0 === c ? Yc.IEnvironment.PROD : c,
					u = t.redirect_uri,
					l = t.useLocalStorage,
					d = t.logsEnabled,
					p = t.onReady,
					f = t.rideRedirectUri,
					m = t.proxiedCheckToken;
				this.environment = s, this.api_parameters = r || {}, this.client_id = n, this.locale = o || Xc.DEFAULT_LANGUAGE, this.scope = i ? i.replace(/\s/gi, "") : "", this.redirect_uri = u, this.ijt = a, this.useLocalStorage = l, d ? es.default.enableLogging() : es.default.disableLogging(), this.onReady = p || null, this.rideRedirectUri = f, this.proxiedCheckToken = m, this.fillAnalyticsParameters(t), Jc.default.loadEnvironment(s, m, window.location.hostname)
			}
			return e.prototype.fillAnalyticsParameters = function(e) {
				var t = e.analytics,
					r = void 0 === t ? {} : t,
					n = r.appCode,
					o = void 0 === n ? "" : n,
					i = r.appVersion,
					a = void 0 === i ? "" : i,
					c = this.analyticsParameters;
				c.appCode = o, c.appVersion = a
			}, e.prototype.createSocialProviderRedirectRequest = function(e, t, r, n, o) {
				var i = {
						idp_flow: "social.deep_link.web",
						provider_id: e
					},
					a = $c($c({}, t), i);
				return this.createRedirectRequest(a, r, n, o)
			}, e.prototype.createReAuthenticateRedirectRequest = function(e, t, r, n, o) {
				void 0 === n && (n = ts.IReauth.check), void 0 === o && (o = rs.IGrantTypes.token);
				var i = {
						reauth: n
					},
					a = $c($c({}, e), i);
				return this.createRedirectRequest(a, t, r, o)
			}, e.prototype.createSignUpRedirectRequest = function(e, t, r) {
				var n = $c($c({}, e), {
					idp_flow: "create_account"
				});
				return this.createRedirectRequest(n, t, r, rs.IGrantTypes.token)
			}, e.prototype.createRedirectRequest = function(e, t, r, n) {
				var o = this,
					i = this,
					a = i.api_parameters,
					c = void 0 === a ? {} : a,
					s = i.client_id,
					u = i.redirect_uri,
					l = void 0 === u ? "" : u,
					d = i.scope,
					p = i.locale,
					f = this.createRedirectState(t, r),
					m = {
						adobeIdRedirectUri: l,
						apiParameters: c,
						clientId: s,
						externalParameters: e,
						scope: d,
						locale: p,
						response_type: n,
						state: f
					};
				return n === rs.IGrantTypes.token ? Promise.resolve(m) : (new ns.CodeChallenge).createCodeChallenge(r).then((function(n) {
					e.code_challenge = n.challenge, e.code_challenge_method = "S256";
					var i = o.createRedirectState(t, r);
					return m.state = i, Promise.resolve(m)
				}))
			}, e.prototype.createRedirectState = function(e, t) {
				var r = this.analyticsParameters,
					n = r.appCode,
					o = void 0 === n ? "" : n,
					i = r.appVersion,
					a = void 0 === i ? "" : i,
					c = void 0 === e ? {} : {
						context: e
					};
				return o && (c.ac = o), a && (c.av = a), c.jslibver = Jc.default.jslibver, c.nonce = t, Object.keys(c).length ? c : null
			}, e.prototype.triggerOnReady = function() {
				this.onReady && this.onReady(void 0)
			}, e.prototype.computeRideRedirectUri = function(e) {
				return this.rideRedirectUri ? "string" == typeof this.rideRedirectUri ? "DEFAULT" === this.rideRedirectUri ? null : this.rideRedirectUri : this.rideRedirectUri(e) : window.location.href
			}, e
		}();
	qc.AdobeIdThinData = os;
	var is = t && t.__extends || function() {
			var e = function(t, r) {
				return e = Object.setPrototypeOf || {
					__proto__: []
				}
				instanceof Array && function(e, t) {
					e.__proto__ = t
				} || function(e, t) {
					for (var r in t) t.hasOwnProperty(r) && (e[r] = t[r])
				}, e(t, r)
			};
			return function(t, r) {
				function n() {
					this.constructor = t
				}
				e(t, r), t.prototype = null === r ? Object.create(r) : (n.prototype = r.prototype, new n)
			}
		}(),
		as = t && t.__assign || function() {
			return as = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, as.apply(this, arguments)
		};
	Object.defineProperty(Fc, "__esModule", {
		value: !0
	});
	var cs = Ia,
		ss = jc,
		us = Uc,
		ls = Hc,
		ds = Vc,
		ps = function(e) {
			function t(t) {
				void 0 === t && (t = null);
				var r = e.call(this, t) || this;
				r.onAccessTokenHasExpired = null, r.onAccessToken = null, r.onReauthAccessToken = null, r.onError = null, r.handlers = {
					triggerOnAccessToken: function(e) {
						r.onAccessToken && r.onAccessToken(e)
					},
					triggerOnReauthAccessToken: function(e) {
						r.onReauthAccessToken && r.onReauthAccessToken(e)
					},
					triggerOnAccessTokenHasExpired: function() {
						r.onAccessTokenHasExpired && r.onAccessTokenHasExpired()
					},
					triggerOnReady: function(e) {
						void 0 === e && (e = null), r.onReady && r.onReady(e)
					},
					triggerOnError: function(e, t) {
						r.onError && r.onError(e, t)
					}
				};
				var n = t || window[cs.AdobeIdKey];
				if (!n || !n.client_id) throw new Error("Please provide required adobeId, client_id information");
				var o = n.standalone,
					i = n.autoValidateToken,
					a = n.modalSettings,
					c = void 0 === a ? {} : a,
					s = n.modalMode,
					u = void 0 !== s && s,
					l = n.onAccessToken,
					d = n.onReauthAccessToken,
					p = n.onAccessTokenHasExpired,
					f = n.onReady,
					m = n.onError,
					g = n.overrideErrorHandler,
					h = n.onModalModeSignInComplete;
				return o && o.token && (r.standalone = new us.StandaloneToken(o)), r.modalSettings = new ss.PopupSettings(c), r.modalMode = u, r.autoValidateToken = !!i, r.onAccessToken = l || null, r.onReauthAccessToken = d || null, r.onAccessTokenHasExpired = p || null, r.onReady = f || null, r.onError = m || null, r.overrideErrorHandler = g, r.onModalModeSignInComplete = h, r
			}
			return is(t, e), t.prototype.createSocialProviderRedirectRequest = function(e, t, r, n, o) {
				void 0 === o && (o = ds.IGrantTypes.token);
				var i = {
						idp_flow: "social.deep_link.web",
						provider_id: e
					},
					a = as(as({}, t), i);
				return this.createRedirectRequest(a, r, n, o)
			}, t.prototype.createReAuthenticateRedirectRequest = function(e, t, r, n, o) {
				void 0 === n && (n = ls.IReauth.check), void 0 === o && (o = ds.IGrantTypes.token);
				var i = {
						reauth: n
					},
					a = as(as({}, e), i);
				return this.createRedirectRequest(a, t, r, o)
			}, t.prototype.createSignUpRedirectRequest = function(e, t, r, n) {
				void 0 === n && (n = ds.IGrantTypes.token);
				var o = as(as({}, e), {
					idp_flow: "create_account"
				});
				return this.createRedirectRequest(o, t, r, n)
			}, t
		}(qc.AdobeIdThinData);
	Fc.AdobeIdData = ps;
	var fs = {},
		ms = {},
		gs = {},
		hs = t && t.__assign || function() {
			return hs = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, hs.apply(this, arguments)
		};
	Object.defineProperty(gs, "__esModule", {
		value: !0
	});
	var vs = function() {
		function e() {
			var e = this;
			this.DEBOUNCE_TIME = 1e3, this.cache = {}, this.storeApiResponse = function(t, r, n) {
				void 0 === t && (t = ""), void 0 === r && (r = ""), e.cacheApiResponse(t, r, n)
			}
		}
		return e.prototype.getCachedApiResponse = function(e, t) {
			void 0 === t && (t = "");
			var r = "string" == typeof t ? t : JSON.stringify(t),
				n = this.cache[e];
			if (!n) return null;
			var o = n[r];
			return o ? o.data : null
		}, e.prototype.cacheApiResponse = function(e, t, r) {
			void 0 === e && (e = ""), void 0 === t && (t = "");
			var n = this.cache[e];
			n || (n = {}, this.cache[e] = n);
			var o = this.createClearCachedDataTimer(e, t);
			n[t] = {
				timerId: o,
				data: hs({}, r)
			}
		}, e.prototype.createClearCachedDataTimer = function(e, t) {
			var r = this;
			return setTimeout((function() {
				var n = r.cache[e] || {},
					o = n[t];
				o && o && o.timerId && (clearTimeout(o.timerId), delete n[t])
			}), this.DEBOUNCE_TIME)
		}, e
	}();
	gs.default = new vs;
	var bs = {},
		ys = {};
	Object.defineProperty(ys, "__esModule", {
		value: !0
	});
	var _s = function(e, t, r) {
		void 0 === r && (r = !1), this.jump = "", this.code = e, this.jump = t, this.isPbaExpiredIdleSessionWorkaround = r
	};
	ys.RideException = _s;
	var ws = {};
	Object.defineProperty(ws, "__esModule", {
		value: !0
	});
	var ks = function(e) {
		var t = e.error,
			r = e.retryAfter,
			n = void 0 === r ? 0 : r,
			o = e.message,
			i = void 0 === o ? "" : o;
		this.error = t, this.retryAfter = n, this.message = i
	};
	ws.HttpErrorResponse = ks, Object.defineProperty(bs, "__esModule", {
		value: !0
	});
	var xs = qc,
		Ts = ys,
		Ss = ws,
		Is = function() {
			function e() {
				this.adobeIdThinData = null
			}
			return e.prototype.verify = function(e, t) {
				void 0 === t && (t = "");
				var r = e.status,
					n = e.data;
				if (!r) return new Ss.HttpErrorResponse({
					error: "networkError",
					message: n || ""
				});
				if (401 == r) return new Ss.HttpErrorResponse({
					error: "unauthorized"
				});
				var o = this.parseTokenResponseForRideErrors(n, t);
				return o || (409 == r ? n : 429 == r ? new Ss.HttpErrorResponse({
					error: "rate_limited",
					retryAfter: n.retryAfter ? parseInt(n.retryAfter) : 10
				}) : r.toString().match(/5\d{2}/g) ? new Ss.HttpErrorResponse({
					error: "server_error"
				}) : null)
			}, e.prototype.parseTokenResponseForRideErrors = function(e, t) {
				if (!e) return null;
				var r = e.error,
					n = e.jump;
				if (!r) return null;
				if (!(0 === r.indexOf("ride_"))) return "token_expired" === r && t.indexOf("check/v6/token") >= 0 ? new Ts.RideException("ride_pba_idle_session", "", !0) : null;
				var o = this.addRedirectUriToJump(r, n);
				return new Ts.RideException(r, o)
			}, e.prototype.addRedirectUriToJump = function(e, t) {
				if (!t || "string" != typeof t) return "";
				var r = t;
				this.adobeIdThinData || (this.adobeIdThinData = new xs.AdobeIdThinData);
				var n = this.adobeIdThinData.computeRideRedirectUri(e);
				if (!n || 0 === n.length) return r;
				try {
					var o = new URL(r);
					return o.searchParams.append("redirect_uri", n), o.toString()
				} catch (e) {
					return r
				}
			}, e.prototype.isUnauthorizedException = function(e) {
				var t = e.status;
				return 401 === (void 0 === t ? 0 : t)
			}, e
		}();
	bs.default = new Is;
	var Es = {},
		Ps = {};
	Object.defineProperty(Ps, "__esModule", {
		value: !0
	});
	var Ds = function() {
		function e(e, t) {
			this.status = 0, this.data = "", this.status = e, this.data = this.toJson(t)
		}
		return e.prototype.toJson = function(e) {
			try {
				return "string" != typeof e ? e : JSON.parse(e)
			} catch (t) {
				return e
			}
		}, e
	}();
	Ps.ApiResponse = Ds, Object.defineProperty(Es, "__esModule", {
		value: !0
	});
	var Cs = Ps;
	Es.default = new(function() {
		function e() {}
		return e.prototype.http = function(e) {
			return new Promise((function(t, r) {
				var n = new(0, window.XMLHttpRequest);
				n.withCredentials = !0, n.open(e.method, e.url, !0);
				var o;
				n.onload = function() {
					return this.status >= 200 && this.status < 300 ? t(new Cs.ApiResponse(this.status, this.response)) : r(new Cs.ApiResponse(this.status, this.response))
				}, n.onerror = function() {
					var e = new Cs.ApiResponse(this.status, this.response);
					return r(e)
				}, (o = e.headers) && Object.keys(o).forEach((function(e) {
					n.setRequestHeader(e, o[e])
				})), n.send(e.data)
			}))
		}, e.prototype.post = function(e, t, r) {
			return void 0 === r && (r = {}), this.http({
				headers: r,
				method: "POST",
				url: e,
				data: t
			})
		}, e.prototype.get = function(e, t) {
			return void 0 === t && (t = {}), this.http({
				headers: t,
				method: "GET",
				url: e
			})
		}, e
	}());
	var As = t && t.__importDefault || function(e) {
		return e && e.__esModule ? e : {
			default: e
		}
	};
	Object.defineProperty(ms, "__esModule", {
		value: !0
	});
	var Os = As(gs),
		Bs = As(bs),
		Rs = As(Es);
	ms.default = new(function() {
		function e() {
			this.triggerOnError = null
		}
		return e.prototype.post = function(e, t, r) {
			var n = this;
			void 0 === r && (r = {});
			var o = Os.default.getCachedApiResponse(e, t);
			if (o) {
				var i = o.status,
					a = o.data;
				return 200 === i ? Promise.resolve(a) : Promise.reject(a)
			}
			return Rs.default.post(e, t, r).then((function(r) {
				return n.storeApiResponse(e, JSON.stringify(t), r)
			})).catch((function(r) {
				return n.verifyError(e, JSON.stringify(t), r)
			}))
		}, e.prototype.get = function(e, t) {
			var r = this;
			void 0 === t && (t = {});
			var n = Os.default.getCachedApiResponse(e);
			if (n) {
				var o = n.status,
					i = n.data;
				return 200 === o ? Promise.resolve(i) : Promise.reject(i)
			}
			return Rs.default.get(e, t).then((function(t) {
				return r.storeApiResponse(e, "", t)
			})).catch((function(t) {
				return r.verifyError(e, "", t)
			}))
		}, e.prototype.verifyError = function(e, t, r) {
			this.storeApiResponse(e, t, r);
			var n = Bs.default.verify(r, e);
			return Promise.reject(n || r.data)
		}, e.prototype.storeApiResponse = function(e, t, r) {
			return void 0 === t && (t = ""), Os.default.storeApiResponse(e, t, r), Promise.resolve(r.data)
		}, e
	}());
	var Ms = t && t.__assign || function() {
			return Ms = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, Ms.apply(this, arguments)
		},
		zs = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(fs, "__esModule", {
		value: !0
	});
	var Fs = Ia,
		js = zs(Fa),
		Ls = zs(Ya),
		Us = zs(Na),
		Ns = zs(ms),
		Hs = function() {
			function e(e) {
				void 0 === e && (e = {}), this.CONTENT_FORM_ENCODED = "application/x-www-form-urlencoded;charset=utf-8", this.apiParameters = e
			}
			return e.prototype.validateToken = function(e) {
				var t = e.token,
					r = e.client_id,
					n = js.default.uriEncodeData(Ms(Ms({}, Ls.default.getCustomApiParameters(this.apiParameters, "validate_token")), {
						type: "access_token",
						client_id: r,
						token: t
					})),
					o = Us.default.baseUrlAdobe + "/ims/validate_token/v1?jslVersion=" + Us.default.jslibver,
					i = this.formEncoded();
				return this.addClientIdInHeader(r, i), Ns.default.post(o, n, i)
			}, e.prototype.getProfile = function(e) {
				var t = e.token,
					r = e.client_id,
					n = Ms({}, Ls.default.getCustomApiParameters(this.apiParameters, "profile")),
					o = this.createAuthorizationHeader(t);
				this.addClientIdInHeader(r, o);
				var i = js.default.uriEncodeData(Ms({
						client_id: r
					}, n)),
					a = Us.default.baseUrlAdobe + "/ims/profile/v1?" + i + "&jslVersion=" + Us.default.jslibver;
				return Ns.default.get(a, o)
			}, e.prototype.getUserInfo = function(e) {
				var t = e.token,
					r = e.client_id,
					n = Ms({}, Ls.default.getCustomApiParameters(this.apiParameters, "userinfo")),
					o = this.createAuthorizationHeader(t);
				this.addClientIdInHeader(r, o);
				var i = js.default.uriEncodeData(Ms({
						client_id: r
					}, n)),
					a = Us.default.baseUrlAdobe + "/ims/userinfo/v1?" + i + "&jslVersion=" + Us.default.jslibver;
				return Ns.default.get(a, o)
			}, e.prototype.logoutToken = function(e) {
				var t = e.client_id,
					r = e.token,
					n = Ms({}, Ls.default.getCustomApiParameters(this.apiParameters, "logout_token")),
					o = Us.default.baseUrlServices + "/ims/logout/v1?jslVersion=" + Us.default.jslibver,
					i = this.addClientIdInHeader(t);
				return Ns.default.post(o, Ms({
					client_id: t,
					access_token: r
				}, n), i)
			}, e.prototype.checkStatus = function() {
				var e = Us.default.baseUrlServices + "/ims/check/v1/status";
				return Ns.default.get(e)
			}, e.prototype.checkToken = function(e, t, r) {
				var n = e.client_id,
					o = e.scope,
					i = Ms({}, Ls.default.mergeExternalParameters(t, this.apiParameters, "check_token")),
					a = Ms(Ms({}, i), {
						client_id: n,
						scope: o
					});
				return r && (a.user_id = r), this.callCheckToken(js.default.uriEncodeData(a), n, "/check/v6/token?jslVersion=" + Us.default.jslibver)
			}, e.prototype.switchProfile = function(e, t, r) {
				void 0 === r && (r = "");
				var n = e.client_id,
					o = e.scope,
					i = void 0 === o ? "" : o,
					a = Ms({}, Ls.default.mergeExternalParameters(t, this.apiParameters, "check_token")),
					c = js.default.uriEncodeData(Ms(Ms({}, a), {
						client_id: n,
						scope: i,
						user_id: r
					}));
				return this.callCheckToken(c, n, "/check/v6/token?jslVersion=" + Us.default.jslibver)
			}, e.prototype.listSocialProviders = function(e) {
				var t = e.client_id,
					r = Ms({}, Ls.default.getCustomApiParameters(this.apiParameters, "providers")),
					n = js.default.uriEncodeData(Ms({
						client_id: t
					}, r)),
					o = Us.default.baseUrlServices + "/ims/social/v1/providers?" + n + "&jslVersion=" + Us.default.jslibver,
					i = this.addClientIdInHeader(t);
				return Ns.default.get(o, i)
			}, e.prototype.exchangeIjt = function(e, t) {
				var r = e.client_id,
					n = Ms({}, Ls.default.getCustomApiParameters(this.apiParameters, "ijt")),
					o = Us.default.baseUrlServices + "/ims/jump/implicit/" + t,
					i = js.default.uriEncodeData(Ms({
						client_id: r
					}, n)),
					a = o + "?" + i + "&jslVersion=" + Us.default.jslibver;
				a.length > 2048 && (delete n.redirect_uri, a = o + "?" + (i = js.default.uriEncodeData(n)));
				var c = this.addClientIdInHeader(r);
				return Ns.default.get(a, c)
			}, e.prototype.avatarUrl = function(e) {
				return Us.default.baseUrlAdobe + "/ims/avatar/download/" + e
			}, e.prototype.getReleaseFlags = function(e) {
				var t = e.token,
					r = e.client_id,
					n = Ms({}, Ls.default.getCustomApiParameters(this.apiParameters, "fg_value")),
					o = this.createAuthorizationHeader(t);
				this.addClientIdInHeader(r, o);
				var i = js.default.uriEncodeData(Ms({
						client_id: r
					}, n)),
					a = Us.default.baseUrlAdobe + "/ims/fg/value/v1?" + i + "&jslVersion=" + Us.default.jslibver;
				return Ns.default.get(a, o)
			}, e.prototype.getTransitoryAuthorizationCode = function(e, t, r) {
				void 0 === t && (t = {});
				var n = Ms({}, Ls.default.mergeExternalParameters(t, this.apiParameters, "check_token")),
					o = js.default.uriEncodeData(Ms(Ms({}, n), e));
				return this.callCheckToken(o, r, "/check/v6/token?client_id=" + r + "&jslVersion=" + Us.default.jslibver)
			}, e.prototype.getTokenFromCode = function(e, t) {
				void 0 === t && (t = {});
				var r = Ms({}, Ls.default.mergeExternalParameters(t, this.apiParameters, "token"));
				r.grant_type = "authorization_code", delete e.other;
				var n = Us.default.baseUrlServices + "/ims/token/v3?jslVersion=" + Us.default.jslibver,
					o = js.default.uriEncodeData(Ms(Ms({}, r), e)),
					i = this.formEncoded();
				return this.addClientIdInHeader(e.client_id, i), Ns.default.post(n, o, i)
			}, e.prototype.jumpToken = function(e, t, r) {
				void 0 === t && (t = {});
				var n = Ms({}, Ls.default.mergeExternalParameters(t, this.apiParameters, "jumptoken")),
					o = Us.default.baseUrlServices + "/ims/jumptoken/v1?client_id=" + r + "&jslVersion=" + Us.default.jslibver,
					i = js.default.uriEncodeData(Ms(Ms({}, n), e)),
					a = this.formEncoded();
				return this.addClientIdInHeader(r, a), Ns.default.post(o, i, a)
			}, e.prototype.socialHeadlessSignIn = function(e, t) {
				void 0 === t && (t = {});
				var r = Ms({}, Ls.default.mergeExternalParameters(t, this.apiParameters, "jumptoken")),
					n = Us.default.baseUrlServices + "/ims/social/v2/native?jslVersion=" + Us.default.jslibver,
					o = js.default.uriEncodeData(Ms(Ms(Ms({}, r), e), {
						response_type: "implicit_jump"
					}));
				return Ns.default.post(n, o, this.formEncoded())
			}, e.prototype.createAuthorizationHeader = function(e) {
				var t = {};
				return e && (t[Fs.HEADERS.AUTHORIZATION] = "Bearer " + e), t
			}, e.prototype.formEncoded = function(e) {
				return void 0 === e && (e = {}), e["content-type"] = this.CONTENT_FORM_ENCODED, e
			}, e.prototype.addClientIdInHeader = function(e, t) {
				return void 0 === t && (t = {}), t.client_id = e, t
			}, e.prototype.callCheckToken = function(e, t, r) {
				var n = this.formEncoded();
				return this.addClientIdInHeader(t, n), Ns.default.post(Us.default.checkTokenEndpoint.url + "/ims" + r, e, n).catch((function(t) {
					if (!Us.default.checkTokenEndpoint.shouldFallbackToAdobe(t)) throw t;
					return Ns.default.post(Us.default.checkTokenEndpoint.fallbackUrl + "/ims" + r, e, n)
				}))
			}, e
		}();
	fs.ImsApis = Hs;
	var Vs = {},
		qs = {};
	! function(e) {
		Object.defineProperty(e, "__esModule", {
				value: !0
			}),
			function(e) {
				e.INITIALIZE_ERROR = "initialize_error", e.HTTP = "http", e.FRAGMENT = "fragment", e.CSRF = "csrf", e.NOT_ALLOWED = "not_allowed", e.PROFILE_EXCEPTION = "profile_exception", e.TOKEN_EXPIRED = "token_expired", e.SOCIAL_PROVIDERS = "SOCIAL_PROVIDERS", e.RIDE_EXCEPTION = "ride_exception"
			}(e.IErrorType || (e.IErrorType = {}))
	}(qs), Object.defineProperty(Vs, "__esModule", {
		value: !0
	});
	var Ws = qs,
		Gs = function(e) {
			this.message = null, this.errorType = Ws.IErrorType.PROFILE_EXCEPTION, this.message = e
		};
	Vs.ProfileException = Gs;
	var Ks = {},
		$s = {};
	Object.defineProperty($s, "__esModule", {
		value: !0
	});
	var Qs = ",";
	$s.sortScopes = function(e) {
		return e.split(Qs).sort().join(Qs)
	}, $s.validateScopeInclusion = function(e, t) {
		var r = (null == t ? void 0 : t.split(Qs)) || [];
		return ((null == e ? void 0 : e.split(Qs)) || []).every((function(e) {
			return r.includes(e)
		}))
	};
	var Xs = t && t.__importDefault || function(e) {
		return e && e.__esModule ? e : {
			default: e
		}
	};
	Object.defineProperty(Ks, "__esModule", {
		value: !0
	});
	var Ys = Xs(xa),
		Js = Ia,
		Zs = Vs,
		eu = ws,
		tu = $s,
		ru = function() {
			function e(e) {
				this.profileServiceRequest = e, this.storage = Ys.default.getStorageByName(Js.STORAGE_MODE.SessionStorage)
			}
			return e.prototype.getProfile = function(e) {
				var t = this,
					r = this.profileServiceRequest,
					n = r.clientId,
					o = r.imsApis,
					i = this.getProfileFromStorage();
				return i ? Promise.resolve(i) : o.getProfile({
					client_id: n,
					token: e
				}).then((function(e) {
					if (!e) throw new Zs.ProfileException("NO profile response");
					if (0 === Object.keys(e).length) throw new Zs.ProfileException("NO profile value");
					return t.saveProfileToStorage(e), Promise.resolve(e)
				})).catch((function(e) {
					return e instanceof eu.HttpErrorResponse || t.removeProfile(), Promise.reject(e)
				}))
			}, e.prototype.getProfileStorageKey = function() {
				var e = this.profileServiceRequest,
					t = e.clientId,
					r = e.scope;
				return Js.PROFILE_STORAGE_KEY + "/" + t + "/" + !1 + "/" + tu.sortScopes(r)
			}, e.prototype.getProfileFromStorage = function() {
				var e = this.getProfileStorageKey(),
					t = this.storage.getItem(e);
				return t && JSON.parse(t)
			}, e.prototype.saveProfileToStorage = function(e) {
				var t = this.getProfileStorageKey();
				this.storage.setItem(t, JSON.stringify(e))
			}, e.prototype.removeProfile = function() {
				var e = this.getProfileStorageKey();
				this.storage.removeItem(e)
			}, e.prototype.removeProfileIfOtherUser = function(e) {
				if (e) {
					var t = this.getProfileFromStorage();
					t && t.userId !== e && this.removeProfile()
				}
			}, e
		}();
	Ks.ProfileService = ru;
	var nu = {},
		ou = {},
		iu = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(ou, "__esModule", {
		value: !0
	});
	var au = iu(Ya),
		cu = iu(Ea),
		su = $s,
		uu = function() {
			function e(e, t) {
				var r = this;
				this.REAUTH_SCOPE = "reauthenticated", this.valid = !1, this.isReauth = function() {
					return r.scope.indexOf(r.REAUTH_SCOPE) >= 0
				}, this.client_id = "", this.scope = "", this.expire = new Date, this.user_id = "", this.tokenValue = "", this.sid = "", this.state = null, this.fromFragment = !1, this.impersonatorId = "", this.isImpersonatedSession = !1;
				var n = e.valid,
					o = e.tokenValue,
					i = e.access_token,
					a = e.state,
					c = e.other,
					s = o || i,
					u = this.parseJwt(s);
				if (!u) throw new Error("token cannot be decoded " + s);
				this.state = au.default.toJson(a);
				var l = u.client_id,
					d = u.user_id,
					p = u.scope,
					f = u.sid,
					m = u.imp_id,
					g = u.imp_sid,
					h = u.pba;
				this.client_id = l, this.expire = t, this.user_id = d, this.scope = p, this.valid = n, this.tokenValue = s, this.sid = f, this.other = c, this.impersonatorId = m || "", this.isImpersonatedSession = !!g, this.pbaSatisfiedPolicies = h && h.split(",") || []
			}
			return e.prototype.parseJwt = function(e) {
				if (!e) return null;
				try {
					return JSON.parse(atob(e.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")))
				} catch (t) {
					return cu.default.error("error on decoding token ", e, t), null
				}
			}, e.prototype.validate = function(e, t) {
				var r = this,
					n = r.valid,
					o = r.client_id,
					i = r.scope,
					a = r.expire;
				return a < new Date ? (cu.default.error("token invalid  --\x3e expires_at", a), !1) : null == n || n ? o !== e ? (cu.default.error("token invalid  --\x3e client id", o, e), !1) : !!su.validateScopeInclusion(t, i) || (cu.default.error("token invalid  --\x3e scope", " token scope =", i, "vs adobeIdScope =", t, "."), !1) : (cu.default.error("token invalid  --\x3e valid"), !1)
			}, e
		}();
	ou.TokenFields = uu;
	var lu = {},
		du = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(lu, "__esModule", {
		value: !0
	});
	var pu = du(Ya),
		fu = function() {
			function e() {}
			return e.prototype.fragmentToObject = function(e) {
				var t = this.getHashFromURL(e);
				if (!t) return null;
				var r = this.processHashUrl(t),
					n = this.getOldHash(r),
					o = n ? r.slice(r.indexOf("old_hash")) : r,
					i = this.removeOldHash(o),
					a = this.getQueryParamsAsMap(i);
				n && (a.old_hash = n);
				var c = a.state;
				return c && (a.state = pu.default.toJson(c)), a
			}, e.prototype.getOldHash = function(e) {
				if (!e) return "";
				var t = e.match("old_hash=(.*?)&from_ims=true");
				return t ? t[1] : ""
			}, e.prototype.removeOldHash = function(e) {
				return e ? e.replace(/old_hash=(.*?)&from_ims=true/gi, "from_ims=true") : e
			}, e.prototype.getHashFromURL = function(e) {
				void 0 === e && (e = window.location.href);
				var t = e.indexOf("#");
				return -1 !== t ? e.substring(t + 1) : ""
			}, e.prototype.getQueryParamsAsMap = function(e) {
				if (!e) return {};
				var t = {};
				return (e = e.replace(/^(#\/|\/|#|\?|&)/, "")).split("&").forEach((function(e) {
					if (e.length) {
						var r = e.split("=");
						t[r[0]] = decodeURIComponent(r[1])
					}
				})), t
			}, e.prototype.processHashUrl = function(e) {
				return e.replace("?error", "#error").replace(/#/gi, "&").replace("from_ims=true?", "from_ims=true&")
			}, e
		}();
	lu.default = new fu;
	var mu = {};
	Object.defineProperty(mu, "__esModule", {
		value: !0
	});
	var gu = function(e, t) {
		this.message = "", this.type = "", this.type = e, this.message = t
	};
	mu.FragmentException = gu;
	var hu = {};
	! function(e) {
		Object.defineProperty(e, "__esModule", {
				value: !0
			}),
			function(e) {
				e.FRAGMENT = "fragment", e.CSRF = "csrf", e.NOT_AUTHORIZE = "not_authorize", e.API_NOT_ALLOWED = "not_allowed"
			}(e.IFragmentExceptionType || (e.IFragmentExceptionType = {}))
	}(hu);
	var vu = {};
	Object.defineProperty(vu, "__esModule", {
		value: !0
	});
	var bu = function(e, t) {
		this.profile = null, this.tokenFields = e, this.profile = t
	};
	vu.TokenProfileResponse = bu;
	var yu = {};
	Object.defineProperty(yu, "__esModule", {
		value: !0
	});
	var _u = function(e) {
		this.exception = null, this.exception = e
	};
	yu.TokenExpiredException = _u;
	var wu = {},
		ku = t && t.__rest || function(e, t) {
			var r = {};
			for (var n in e) Object.prototype.hasOwnProperty.call(e, n) && t.indexOf(n) < 0 && (r[n] = e[n]);
			if (null != e && "function" == typeof Object.getOwnPropertySymbols) {
				var o = 0;
				for (n = Object.getOwnPropertySymbols(e); o < n.length; o++) t.indexOf(n[o]) < 0 && Object.prototype.propertyIsEnumerable.call(e, n[o]) && (r[n[o]] = e[n[o]])
			}
			return r
		};
	Object.defineProperty(wu, "__esModule", {
		value: !0
	});
	var xu = function(e) {
		this.client_id = "", this.scope = "", this.code = "", this.state = null, this.code_verifier = "", this.other = null;
		var t = e.code,
			r = e.state,
			n = e.client_id,
			o = e.scope,
			i = e.verifier,
			a = ku(e, ["code", "state", "client_id", "scope", "verifier"]);
		this.state = r, this.client_id = n, this.code = t, this.scope = o, this.code_verifier = i, this.other = a
	};
	wu.AuthorizationCode = xu;
	var Tu = {};
	Object.defineProperty(Tu, "__esModule", {
		value: !0
	});
	var Su = function(e) {
		this.wndRedirectPropName = "", this.wndRedirectPropName = e
	};
	Tu.ModalSignInEvent = Su;
	var Iu = {};
	Object.defineProperty(Iu, "__esModule", {
		value: !0
	});
	var Eu = "abcdefghijklmnopqrstuvwxyz234567".split("").reduce((function(e, t, r) {
			return e[t] = r, e
		}), {
			"=": 0
		}),
		Pu = function(e) {
			return function(e, t, r) {
				var n = t - r.length;
				return n > 0 && (r = new Array(n + 1).join(e) + r), r
			}("0", 5, e.toString(2))
		};
	Iu.decodeToBitstring = function(e, t) {
		if (void 0 === t && (t = !1), "string" != typeof e) throw new Error("Data is not a string");
		var r = e.toLowerCase().split("");
		! function(e) {
			if (e.length % 8 != 0) throw new Error("Data length is not a multiple of 8");
			e.forEach((function(e) {
				if (!(e in Eu)) throw new Error("Unknown encoded character " + e)
			}));
			var t = !1;
			e.forEach((function(e) {
				if ("=" !== e && t) throw new Error("Found padding char in the middle of the string");
				"=" === e && (t = !0)
			}))
		}(r);
		var n = function(e) {
				for (var t = e.length - 1, r = 0;
					"=" === e[t];) ++r, --t;
				return r
			}(r),
			o = [];
		r.forEach((function(e) {
			o.push(Pu(Eu[e]))
		}));
		var i = o.join("");
		return n > 0 && (i = i.slice(0, -5 * n)), i.length % 8 != 0 && (i = i.slice(0, i.length % 8 * -1)), t ? function(e) {
			var t = "";
			if (e.length % 8 != 0) throw new Error("Length must be a multiple of 8");
			for (var r = 0, n = e.length; r < n; r += 8) t += e.slice(r, r + 8).split("").reverse().join("");
			return t
		}(i) : i
	};
	var Du = t && t.__assign || function() {
			return Du = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, Du.apply(this, arguments)
		},
		Cu = t && t.__rest || function(e, t) {
			var r = {};
			for (var n in e) Object.prototype.hasOwnProperty.call(e, n) && t.indexOf(n) < 0 && (r[n] = e[n]);
			if (null != e && "function" == typeof Object.getOwnPropertySymbols) {
				var o = 0;
				for (n = Object.getOwnPropertySymbols(e); o < n.length; o++) t.indexOf(n[o]) < 0 && Object.prototype.propertyIsEnumerable.call(e, n[o]) && (r[n[o]] = e[n[o]])
			}
			return r
		},
		Au = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(nu, "__esModule", {
		value: !0
	});
	var Ou = Au(xa),
		Bu = Ia,
		Ru = ou,
		Mu = Au(lu),
		zu = mu,
		Fu = hu,
		ju = vu,
		Lu = ys,
		Uu = yu,
		Nu = Au(Ea),
		Hu = ws,
		Vu = wu,
		qu = Tu,
		Wu = Kc,
		Gu = $s,
		Ku = Iu,
		$u = ["authorize", "check_token"],
		Qu = function() {
			function e(e, t) {
				var r = this;
				this.getTokenAndProfile = function(e) {
					void 0 === e && (e = {});
					var t = r.getTokenFields();
					return t instanceof qu.ModalSignInEvent || t instanceof zu.FragmentException ? Promise.reject(t) : t instanceof Vu.AuthorizationCode ? r.tokenServiceRequest.imsApis.getTokenFromCode(t, e).then((function(e) {
						var r = e.access_token,
							n = e.state,
							o = e.expires_in,
							i = e.scope,
							a = void 0 === i ? "" : i,
							c = Cu(e, ["access_token", "state", "expires_in", "scope"]),
							s = new Ru.TokenFields({
								scope: a,
								tokenValue: r,
								valid: !0,
								state: n,
								other: t.other
							}, new Date((new Date).getTime() + parseFloat(o)));
						return Promise.resolve(new ju.TokenProfileResponse(s, c))
					})) : t instanceof Ru.TokenFields ? t.fromFragment || !r.tokenServiceRequest.autoValidateToken ? Promise.resolve(new ju.TokenProfileResponse(t, null)) : r.callValidateTokenApi(t.tokenValue).then((function() {
						return new ju.TokenProfileResponse(t, null)
					})).catch((function() {
						return r.callRefreshToken(e)
					})) : r.callRefreshToken(e)
				};
				var n = e.useLocalStorage;
				this.csrfService = t, this.tokenServiceRequest = e, this.storage = Ou.default.getStorageByName(n ? Bu.STORAGE_MODE.LocalStorage : Bu.STORAGE_MODE.SessionStorage)
			}
			return e.prototype.getTokenFields = function() {
				var e = this.tokenServiceRequest,
					t = e.clientId,
					r = e.scope,
					n = this.getTokenFromFragment();
				return n instanceof qu.ModalSignInEvent || n instanceof zu.FragmentException || n instanceof Vu.AuthorizationCode ? n : n && n.validate(t, r) ? (n.fromFragment = !0, this.addTokenToStorage(n), n) : this.getTokenFieldsFromStorage()
			}, e.prototype.validateToken = function() {
				var e = this.getTokenFieldsFromStorage();
				return e ? this.callValidateTokenApi(e.tokenValue) : Promise.reject(null)
			}, e.prototype.getReleaseFlags = function() {
				var e = this.tokenServiceRequest,
					t = e.clientId,
					r = e.imsApis,
					n = this.getTokenFieldsFromStorage();
				return n ? r.getReleaseFlags({
					token: n.tokenValue,
					client_id: t
				}) : Promise.reject(null)
			}, e.prototype.getDecodedReleaseFlags = function() {
				return this.getReleaseFlags().then((function(e) {
					return Ku.decodeToBitstring(e.releaseFlags, !0)
				}))
			}, e.prototype.callValidateTokenApi = function(e) {
				var t = this,
					r = this.tokenServiceRequest,
					n = r.clientId,
					o = r.scope;
				return r.imsApis.validateToken({
					client_id: n,
					token: e
				}).then((function(r) {
					Nu.default.info("validateToken response", r);
					var i = new Ru.TokenFields(Du(Du({}, r), {
						tokenValue: e
					}), new Date(parseFloat(r.expires_at)));
					if (i.validate(n, o)) return t.addTokenToStorage(i), Promise.resolve(i);
					throw new Error("could not validate tokenFields")
				})).catch((function(e) {
					return Nu.default.error("validateToken response", e), e instanceof Hu.HttpErrorResponse || t.removeTokenFromLocalStorage(), Promise.reject(e)
				}))
			}, e.prototype.getTokenFromFragment = function(e) {
				var t = Mu.default.fragmentToObject(e);
				if (!t) return null;
				var r = t.access_token,
					n = t.scope,
					o = t.error,
					i = t.api,
					a = t.state,
					c = void 0 === a ? {} : a,
					s = t.expires_in,
					u = t.client_id,
					l = t.code,
					d = void 0 === l ? "" : l,
					p = Cu(t, ["access_token", "scope", "error", "api", "state", "expires_in", "client_id", "code"]),
					f = c || {},
					m = f.imslibmodal,
					g = f.nonce;
				if (!0 === m) return new qu.ModalSignInEvent(g);
				if (!t.from_ims) return null;
				if (u !== this.tokenServiceRequest.clientId) return null;
				if (o) return new zu.FragmentException(Fu.IFragmentExceptionType.FRAGMENT, o);
				if (!$u.includes(i)) return new zu.FragmentException(Fu.IFragmentExceptionType.API_NOT_ALLOWED, "api should be authorize or check token and " + i + " is used");
				if (!this.csrfService.verify(g)) return new zu.FragmentException(Fu.IFragmentExceptionType.CSRF, "CSRF exception");
				if (d) {
					var h = (new Wu.CodeChallenge).getVerifierByKey(g);
					if (!h) throw new Error("no verifier value has been found");
					return new Vu.AuthorizationCode(Du(Du({}, t), {
						verifier: h
					}))
				}
				return r ? new Ru.TokenFields({
					client_id: u,
					scope: n,
					tokenValue: r,
					valid: !0,
					state: c,
					other: p
				}, new Date((new Date).getTime() + parseFloat(s))) : null
			}, e.prototype.getItemFromStorage = function(e) {
				return this.storage.getItem(e)
			}, e.prototype.getTokenFieldsFromStorage = function(e) {
				void 0 === e && (e = !1);
				var t = this.tokenServiceRequest,
					r = t.clientId,
					n = t.scope,
					o = this.getAccessTokenKey(e),
					i = this.getItemFromStorage(o);
				if (!i) return null;
				var a = JSON.parse(i),
					c = a.expire ? new Date(Date.parse(a.expire)) : new Date(a.expiresAtMilliseconds),
					s = new Ru.TokenFields(a, c);
				return s.validate(r, n) ? s : null
			}, e.prototype.getAccessTokenKey = function(e) {
				void 0 === e && (e = !1);
				var t = this.tokenServiceRequest,
					r = t.clientId,
					n = t.scope;
				return Bu.TOKEN_STORAGE_KEY + "/" + r + "/" + e + "/" + Gu.sortScopes(n)
			}, e.prototype.addTokenToStorage = function(e) {
				if (e) {
					var t = e.isReauth(),
						r = this.getAccessTokenKey(t),
						n = Du({}, e);
					n.state = {}, n.other = "{}";
					var o = JSON.stringify(n);
					this.storage.setItem(r, o)
				}
			}, e.prototype.removeTokenFromLocalStorage = function() {
				var e = this.getAccessTokenKey();
				this.storage.removeItem(e)
			}, e.prototype.removeReauthTokenFromLocalStorage = function() {
				var e = this.getAccessTokenKey(!0);
				this.storage.removeItem(e)
			}, e.prototype.refreshToken = function(e) {
				var t = this;
				void 0 === e && (e = {});
				var r = this.tokenServiceRequest,
					n = r.clientId,
					o = r.imsApis,
					i = r.scope,
					a = this.getTokenFieldsFromStorage(),
					c = a ? a.user_id : "";
				return o.checkToken({
					client_id: n,
					scope: i
				}, e, c).then((function(e) {
					if (!e) throw new Error("refresh token --\x3e no response");
					var r = e.access_token,
						n = e.expires_in,
						o = e.token_type,
						i = e.error,
						a = e.error_description,
						c = void 0 === a ? "" : a,
						s = e.sid,
						u = Cu(e, ["access_token", "expires_in", "token_type", "error", "error_description", "sid"]);
					if (i) throw new Error(i + " " + c);
					var l = Object.keys(u).length ? u : null,
						d = {
							token: r,
							expire: new Date(Date.now() + parseFloat(n)),
							token_type: o,
							sid: s
						},
						p = t.updateToken(d) || {},
						f = {
							tokenInfo: Du(Du({}, d), {
								impersonatorId: p.impersonatorId || "",
								isImpersonatedSession: p.isImpersonatedSession || !1,
								pbaSatisfiedPolicies: p.pbaSatisfiedPolicies || []
							}),
							profile: l
						};
					return Promise.resolve(f)
				})).catch((function(e) {
					return void 0 === e && (e = {}), e instanceof Hu.HttpErrorResponse || e instanceof Lu.RideException ? Promise.reject(e) : (t.removeTokenFromLocalStorage(), Promise.reject(new Uu.TokenExpiredException(e)))
				}))
			}, e.prototype.switchProfile = function(e, t) {
				var r = this;
				void 0 === t && (t = {});
				var n = this.tokenServiceRequest,
					o = n.clientId,
					i = n.imsApis,
					a = n.scope;
				return i.switchProfile({
					client_id: o,
					scope: a
				}, t, e).then((function(e) {
					if (!e) throw new Error("refresh token --\x3e no response");
					var t = e.access_token,
						n = e.expires_in,
						o = e.token_type,
						i = e.error,
						a = e.error_description,
						c = void 0 === a ? "" : a,
						s = e.sid,
						u = Cu(e, ["access_token", "expires_in", "token_type", "error", "error_description", "sid"]);
					if (i) throw new Error(i + " " + c);
					var l = Object.keys(u).length ? u : null,
						d = {
							token: t,
							expire: new Date(Date.now() + parseFloat(n)),
							token_type: o,
							sid: s
						},
						p = r.updateToken(d) || {},
						f = {
							tokenInfo: Du(Du({}, d), {
								impersonatorId: p.impersonatorId || "",
								isImpersonatedSession: p.isImpersonatedSession || !1
							}),
							profile: l
						};
					return Promise.resolve(f)
				})).catch((function(e) {
					return void 0 === e && (e = {}), Promise.reject(e)
				}))
			}, e.prototype.callRefreshToken = function(e) {
				return void 0 === e && (e = {}), this.refreshToken(e).then((function(e) {
					var t = e.tokenInfo,
						r = t.token,
						n = t.expire,
						o = e.profile,
						i = new Ru.TokenFields({
							valid: !0,
							tokenValue: r
						}, n),
						a = new ju.TokenProfileResponse(i, o);
					return Promise.resolve(a)
				})).catch((function(e) {
					return Promise.reject(e)
				}))
			}, e.prototype.updateToken = function(e) {
				var t = e.token,
					r = e.expire,
					n = new Ru.TokenFields({
						tokenValue: t
					}, r);
				return n ? (n.tokenValue = t, this.addTokenToStorage(n), n) : null
			}, e.prototype.purge = function() {
				this.removeTokenFromLocalStorage(), this.removeReauthTokenFromLocalStorage()
			}, e.prototype.setStandAloneToken = function(e) {
				var t = e.token,
					r = e.sid,
					n = e.expirems,
					o = void 0 === n ? -1 : n,
					i = this.tokenServiceRequest,
					a = i.clientId,
					c = i.scope,
					s = new Date((new Date).getTime() + o);
				if (!new Ru.TokenFields({
						valid: !0,
						tokenValue: t
					}, s).validate(a, c)) return !1;
				var u = {
					expire: s,
					token: t,
					sid: r
				};
				return this.updateToken(u), !0
			}, e.prototype.exchangeIjt = function(e) {
				var t = this,
					r = this.tokenServiceRequest,
					n = {
						client_id: r.clientId,
						scope: r.scope
					};
				return r.imsApis.exchangeIjt(n, e).then((function(e) {
					var r = e.valid,
						n = e.access_token,
						o = e.expires_in,
						i = e.profile;
					if (!1 === r) return Promise.reject(e);
					var a = new Date(Date.now() + 1e3 * parseFloat(o)),
						c = new Ru.TokenFields({
							valid: !0,
							tokenValue: n
						}, a);
					t.addTokenToStorage(c);
					var s = new ju.TokenProfileResponse(c, i);
					return Promise.resolve(s)
				}))
			}, e
		}();
	nu.TokenService = Qu;
	var Xu = {},
		Yu = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(Xu, "__esModule", {
		value: !0
	});
	var Ju = Yu(Ea),
		Zu = ["keyup", "mousemove"],
		el = function() {
			function e() {
				var e = this;
				this.lastUserInteraction = Date.now(), this.userActive = !1, this.userInteractionHandler = function() {
					e.lastUserInteraction = Date.now(), e.userActive = !0
				}, this.initializeDomEvents = function() {
					Zu.forEach((function(t) {
						return window.addEventListener(t, e.userInteractionHandler)
					}))
				}, this.clearDomEvents = function() {
					Zu.forEach((function(t) {
						return window.removeEventListener(t, e.userInteractionHandler)
					}))
				}
			}
			return e.prototype.startAutoRefreshFlow = function(e) {
				var t, r = this;
				if (e && e.expire && e.refreshTokenMethod) {
					this.refreshParameters = e, this.refreshTimerId && (Ju.default.info("Auto-refresh timer already set, clearing"), clearTimeout(this.refreshTimerId)), this.clearDomEvents(), this.initializeDomEvents();
					var n = this.fromNowToNMinutesBeforeDate(null === (t = this.refreshParameters) || void 0 === t ? void 0 : t.expire, 1);
					Ju.default.info("Auto-refresh timer will run after (seconds)", n / 1e3), this.refreshTimerId = setTimeout((function() {
						var e;
						if (r.userActive) {
							var t = Math.floor((Date.now() - r.lastUserInteraction) / 1e3);
							null === (e = r.refreshParameters) || void 0 === e || e.refreshTokenMethod({
								userInactiveSince: t
							}, !0), Ju.default.info("Auto-refresh performed, user was inactive for (seconds)", t)
						} else Ju.default.info("Auto-refresh skipped, user was never active")
					}), n)
				} else Ju.default.info("Won't schedule token auto-refresh", !e, !e.expire, !e.refreshTokenMethod)
			}, e.prototype.fromNowToNMinutesBeforeDate = function(e, t) {
				var r = e.getTime() - Date.now() - 6e4 * t;
				return r <= 0 ? 6e4 * t : r
			}, e
		}();
	Xu.default = new el;
	var tl = t && t.__assign || function() {
			return tl = Object.assign || function(e) {
				for (var t, r = 1, n = arguments.length; r < n; r++)
					for (var o in t = arguments[r]) Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
				return e
			}, tl.apply(this, arguments)
		},
		rl = t && t.__awaiter || function(e, t, r, n) {
			return new(r || (r = Promise))((function(o, i) {
				function a(e) {
					try {
						s(n.next(e))
					} catch (e) {
						i(e)
					}
				}

				function c(e) {
					try {
						s(n.throw(e))
					} catch (e) {
						i(e)
					}
				}

				function s(e) {
					var t;
					e.done ? o(e.value) : (t = e.value, t instanceof r ? t : new r((function(e) {
						e(t)
					}))).then(a, c)
				}
				s((n = n.apply(e, t || [])).next())
			}))
		},
		nl = t && t.__generator || function(e, t) {
			var r, n, o, i, a = {
				label: 0,
				sent: function() {
					if (1 & o[0]) throw o[1];
					return o[1]
				},
				trys: [],
				ops: []
			};
			return i = {
				next: c(0),
				throw: c(1),
				return: c(2)
			}, "function" == typeof Symbol && (i[Symbol.iterator] = function() {
				return this
			}), i;

			function c(i) {
				return function(c) {
					return function(i) {
						if (r) throw new TypeError("Generator is already executing.");
						for (; a;) try {
							if (r = 1, n && (o = 2 & i[0] ? n.return : i[0] ? n.throw || ((o = n.return) && o.call(n), 0) : n.next) && !(o = o.call(n, i[1])).done) return o;
							switch (n = 0, o && (i = [2 & i[0], o.value]), i[0]) {
								case 0:
								case 1:
									o = i;
									break;
								case 4:
									return a.label++, {
										value: i[1],
										done: !1
									};
								case 5:
									a.label++, n = i[1], i = [0];
									continue;
								case 7:
									i = a.ops.pop(), a.trys.pop();
									continue;
								default:
									if (!(o = a.trys, (o = o.length > 0 && o[o.length - 1]) || 6 !== i[0] && 2 !== i[0])) {
										a = 0;
										continue
									}
									if (3 === i[0] && (!o || i[1] > o[0] && i[1] < o[3])) {
										a.label = i[1];
										break
									}
									if (6 === i[0] && a.label < o[1]) {
										a.label = o[1], o = i;
										break
									}
									if (o && a.label < o[2]) {
										a.label = o[2], a.ops.push(i);
										break
									}
									o[2] && a.ops.pop(), a.trys.pop();
									continue
							}
							i = t.call(e, a)
						} catch (e) {
							i = [6, e], n = 0
						} finally {
							r = o = 0
						}
						if (5 & i[0]) throw i[1];
						return {
							value: i[0] ? i[1] : void 0,
							done: !0
						}
					}([i, c])
				}
			}
		},
		ol = t && t.__importDefault || function(e) {
			return e && e.__esModule ? e : {
				default: e
			}
		};
	Object.defineProperty(wa, "__esModule", {
		value: !0
	});
	var il = ka,
		al = ol(Ea),
		cl = za,
		sl = bc,
		ul = Cc,
		ll = Fc,
		dl = fs,
		pl = Hc,
		fl = Vs,
		ml = Ks,
		gl = nu,
		hl = ol(Fa),
		vl = ol(lu),
		bl = qs,
		yl = yu,
		_l = ys,
		wl = ws,
		kl = ol(Xu),
		xl = Ia,
		Tl = ol(Na),
		Sl = Vc,
		Il = Tu,
		El = Kc,
		Pl = ou,
		Dl = Xa,
		Cl = function() {
			function e(e) {
				var t = this;
				void 0 === e && (e = null), this.initialized = !1, this.onPopupMessage = function(e) {
					if (e && t.adobeIdData.onModalModeSignInComplete) {
						var r = t.tokenService.getTokenFromFragment(e);
						if (r instanceof Pl.TokenFields && (t.tokenService.addTokenToStorage(r), t.adobeIdData.onModalModeSignInComplete(r))) return Promise.resolve()
					}
					return hl.default.replaceUrl(e), t.initialize()
				}, this.signIn = function(e, r, n) {
					return void 0 === e && (e = {}), void 0 === n && (n = Sl.IGrantTypes.token), rl(t, void 0, void 0, (function() {
						var t, o, i, a, c;
						return nl(this, (function(s) {
							switch (s.label) {
								case 0:
									return this.checkInitialized(), o = (t = this).adobeIdData, i = t.csrfService, a = i.initialize(), [4, o.createRedirectRequest(e, r, a, n)];
								case 1:
									return c = s.sent(), this.signInservice.signIn(c), [2, Promise.resolve()]
							}
						}))
					}))
				}, this.authorizeToken = function(e, r, n, o) {
					void 0 === e && (e = ""), void 0 === r && (r = {}), void 0 === o && (o = Sl.IGrantTypes.token);
					var i = t,
						a = i.adobeIdData,
						c = i.csrfService.initialize();
					return a.createRedirectRequest(r, n, c, o).then((function(t) {
						(new cl.SignInService).authorizeToken(e, t)
					}))
				}, this.reAuthenticate = function(e, r, n, o) {
					void 0 === e && (e = {}), void 0 === r && (r = pl.IReauth.check), void 0 === o && (o = Sl.IGrantTypes.token), t.checkInitialized();
					var i = t,
						a = i.adobeIdData,
						c = i.csrfService.initialize();
					return a.createReAuthenticateRedirectRequest(e, n, c, r, o).then((function(e) {
						t.signInservice.signIn(e)
					}))
				}, this.signInWithSocialProvider = function(e, r, n, o) {
					if (void 0 === r && (r = {}), void 0 === o && (o = Sl.IGrantTypes.token), t.checkInitialized(), !e) throw new Error("please provide the provider name");
					var i = t,
						a = i.adobeIdData,
						c = i.csrfService.initialize();
					a.createSocialProviderRedirectRequest(e, r, n, c, o).then((function(e) {
						t.signInservice.signIn(e)
					}))
				}, this.signOut = function(e) {
					void 0 === e && (e = {}), t.checkInitialized(), t.tokenService.purge(), t.profileService.removeProfile();
					var r = t.adobeIdData,
						n = r.api_parameters,
						o = void 0 === n ? {} : n,
						i = r.client_id,
						a = {
							adobeIdRedirectUri: r.redirect_uri,
							apiParameters: o,
							clientId: i,
							externalParameters: e
						};
					(new ul.SignOutService).signOut(a)
				}, this.getTokenForPBAPolicy = function(e, r, n, o) {
					var i;
					void 0 === e && (e = ""), void 0 === r && (r = 1e4), void 0 === o && (o = {});
					var a = t.getAccessToken();
					if (a && Date.now() + r < a.expire.getTime() && (!e || (null === (i = a.pbaSatisfiedPolicies) || void 0 === i ? void 0 : i.includes(e)))) return Promise.resolve(a);
					var c = t,
						s = c.adobeIdData,
						u = c.csrfService.initialize();
					return o.state = s.createRedirectState(n, u), o.redirect_uri = Dl.RedirectHelper.createRedirectUrl(s.redirect_uri, s.client_id, o, "check_token", s.scope), e && (o.pba_policy = e), t.refreshToken(o)
				}, this.refreshToken = function(e, r) {
					if (void 0 === e && (e = {}), void 0 === r && (r = !1), !r && e.userInactiveSince) {
						var n = e.userInactiveSince,
							o = Date.now() - 1e3 * n;
						o > kl.default.lastUserInteraction && (kl.default.lastUserInteraction = o)
					}
					return t.tokenService.refreshToken(e).then((function(e) {
						return t.onTokenProfileReceived(e)
					})).catch((function(e) {
						if (al.default.error("refresh token error", e), e instanceof wl.HttpErrorResponse) return Promise.reject(e);
						var r = t.verifyRideErrorExceptionStrict(e);
						return r || (t.profileService.removeProfile(), t.onTokenExpired(), Promise.reject(e))
					}))
				}, this.switchProfile = function(e, r) {
					return void 0 === r && (r = {}), e ? t.tokenService.switchProfile(e, r).then((function(e) {
						return t.onTokenProfileReceived(e)
					})).catch((function(e) {
						return t.verifyRideErrorException(e)
					})) : Promise.reject(new Error("Please provide the user id for switchProfile"))
				}, this.triggerOnImsInstance = function(e) {
					var r = document.createEvent("CustomEvent"),
						n = {
							clientId: t.adobeIdData.client_id,
							instance: e
						};
					r.initCustomEvent(xl.ON_IMSLIB_INSTANCE, !1, !1, n), window.dispatchEvent(r)
				}, this.processInitializeException = function(e) {
					void 0 === e && (e = {});
					var r = t.adobeIdData.handlers;
					return al.default.warn("initialize", e), e instanceof Il.ModalSignInEvent ? t.notifyParentAboutModalSignIn(e) : (t.restoreHash(), e instanceof yl.TokenExpiredException && r.triggerOnAccessTokenHasExpired(), Promise.reject(e))
				}, this.verifyRideErrorException = function(e) {
					return rl(t, void 0, void 0, (function() {
						return nl(this, (function(t) {
							switch (t.label) {
								case 0:
									return e instanceof _l.RideException ? this.adobeIdData.overrideErrorHandler && !this.adobeIdData.overrideErrorHandler(e) ? [2, Promise.reject(e)] : e.isPbaExpiredIdleSessionWorkaround ? [4, this.signIn()] : [3, 2] : [3, 4];
								case 1:
									return t.sent(), [3, 4];
								case 2:
									return e.jump ? [4, hl.default.replaceUrlAndWait(e.jump, 1e4)] : [3, 4];
								case 3:
									t.sent(), t.label = 4;
								case 4:
									return [2, Promise.reject(e)]
							}
						}))
					}))
				}, this.verifyRideErrorExceptionStrict = function(e) {
					return e instanceof _l.RideException ? t.verifyRideErrorException(e) : null
				}, this.verifyCsrfException = function(e) {
					var r = e.type;
					return r && r === bl.IErrorType.CSRF && t.signOut(), Promise.reject(e)
				}, this.processTokenResponse = function(e) {
					var r = t.adobeIdData.handlers,
						n = e.tokenFields,
						o = e.profile,
						i = n.tokenValue,
						a = n.state,
						c = n.expire,
						s = n.sid,
						u = n.user_id,
						l = n.other,
						d = void 0 === l ? {} : l,
						p = n.impersonatorId,
						f = n.isImpersonatedSession,
						m = n.pbaSatisfiedPolicies;
					al.default.info("token", i), d.from_ims && hl.default.setHash(d.old_hash || ""), t.profileService.removeProfileIfOtherUser(u);
					var g = {
						token: i,
						expire: c,
						sid: s,
						impersonatorId: p,
						isImpersonatedSession: f,
						pbaSatisfiedPolicies: m
					};
					return n.isReauth() ? r.triggerOnReauthAccessToken(g) : t.tokenReceived(g), o && t.profileService.saveProfileToStorage(o), Promise.resolve(a)
				}, this.exchangeIjt = function(e) {
					var r = t.adobeIdData.ijt;
					return e || r ? t.tokenService.exchangeIjt(e || r).then((function(e) {
						return e.profile ? t.profileService.saveProfileToStorage(e.profile) : t.profileService.removeProfile(), Promise.resolve(e)
					})) : Promise.reject(new Error("please set the adobeid.ijt value"))
				}, this.adobeIdData = new ll.AdobeIdData(e);
				var r = this.adobeIdData,
					n = r.api_parameters,
					o = void 0 === n ? {} : n,
					i = r.client_id,
					a = r.scope,
					c = r.useLocalStorage,
					s = r.autoValidateToken,
					u = r.modalMode,
					l = r.modalSettings;
				this.imsApis = new dl.ImsApis(o), this.csrfService = new il.CsrfService(i), this.serviceRequest = {
					clientId: i,
					scope: a,
					imsApis: this.imsApis
				}, this.tokenService = new gl.TokenService(tl(tl({}, this.serviceRequest), {
					useLocalStorage: c,
					autoValidateToken: s
				}), this.csrfService), this.profileService = new ml.ProfileService(this.serviceRequest), this.signInservice = u ? new sl.SignInModalService(this.onPopupMessage, l) : new cl.SignInService
			}
			return Object.defineProperty(e.prototype, "version", {
				get: function() {
					return Tl.default.jslibver
				},
				enumerable: !0,
				configurable: !0
			}), Object.defineProperty(e.prototype, "adobeid", {
				get: function() {
					return tl({}, this.adobeIdData)
				},
				enumerable: !0,
				configurable: !0
			}), e.prototype.enableLogging = function() {
				al.default.enableLogging()
			}, e.prototype.disableLogging = function() {
				al.default.disableLogging()
			}, e.prototype.checkInitialized = function() {
				this.initialized
			}, e.prototype.signUp = function(e, t) {
				var r = this;
				void 0 === e && (e = {}), this.checkInitialized();
				var n = this.adobeIdData,
					o = this.csrfService;
				if (!n) throw new Error("no adobeId on reAuthenticate");
				var i = o.initialize();
				return n.createSignUpRedirectRequest(e, t, i).then((function(e) {
					r.signInservice.signIn(e)
				}))
			}, e.prototype.isSignedInUser = function() {
				return !(!this.getAccessToken() && !this.getReauthAccessToken())
			}, e.prototype.getProfile = function() {
				var e = this,
					t = this.profileService.getProfileFromStorage();
				if (t) return Promise.resolve(t);
				var r = this.getAccessToken() || this.getReauthAccessToken();
				if (!r) {
					return Promise.reject(new fl.ProfileException("please login before getting the profile"))
				}
				return this.profileService.getProfile(r.token).then((function(e) {
					return Promise.resolve(e)
				})).catch((function(t) {
					return al.default.error("get profile exception ", t), t instanceof wl.HttpErrorResponse ? e.refreshToken().then((function(e) {
						return Promise.resolve(e.profile)
					})) : Promise.reject(new fl.ProfileException(t.message || t))
				}))
			}, e.prototype.avatarUrl = function(e) {
				return this.imsApis.avatarUrl(e)
			}, e.prototype.getReleaseFlags = function(e) {
				return void 0 === e && (e = !1), e ? this.tokenService.getDecodedReleaseFlags() : this.tokenService.getReleaseFlags()
			}, e.prototype.getAccessToken = function() {
				return this.getTokenFromStorage(!1)
			}, e.prototype.getReauthAccessToken = function() {
				return this.getTokenFromStorage(!0)
			}, e.prototype.getTokenFromStorage = function(e) {
				var t = this.tokenService.getTokenFieldsFromStorage(e);
				return t ? {
					token: t.tokenValue,
					expire: t.expire,
					sid: t.sid,
					impersonatorId: t.impersonatorId,
					isImpersonatedSession: t.isImpersonatedSession,
					pbaSatisfiedPolicies: t.pbaSatisfiedPolicies
				} : null
			}, e.prototype.listSocialProviders = function() {
				var e = this;
				return new Promise((function(t, r) {
					var n = e.adobeIdData.client_id;
					e.imsApis.listSocialProviders({
						client_id: n
					}).then((function(e) {
						t(e)
					})).catch((function(e) {
						r(e)
					}))
				}))
			}, e.prototype.tokenReceived = function(e) {
				this.adobeIdData.handlers.triggerOnAccessToken(e), kl.default.startAutoRefreshFlow({
					expire: e.expire,
					refreshTokenMethod: this.refreshToken
				})
			}, e.prototype.onTokenProfileReceived = function(e) {
				var t = e.tokenInfo,
					r = e.profile;
				return al.default.info("token", t), this.tokenReceived(t), this.profileService.saveProfileToStorage(r), Promise.resolve(e)
			}, e.prototype.validateToken = function() {
				var e = this;
				return this.tokenService.validateToken().then((function() {
					return Promise.resolve(!0)
				})).catch((function(t) {
					return al.default.warn("validate token exception", t), t instanceof wl.HttpErrorResponse || e.profileService.removeProfile(), Promise.reject(!1)
				}))
			}, e.prototype.onTokenExpired = function() {
				var e = this.adobeIdData.handlers;
				this.tokenService.purge(), e.triggerOnAccessTokenHasExpired()
			}, e.prototype.setStandAloneToken = function(e) {
				return this.tokenService.setStandAloneToken(e)
			}, e.prototype.initialize = function() {
				var e = this,
					t = this.adobeIdData,
					r = t.handlers,
					n = t.standalone,
					o = t.ijt,
					i = null;
				return n && this.setStandAloneToken(n), (o ? this.exchangeIjt : this.tokenService.getTokenAndProfile)().then(this.processTokenResponse, (function(t) {
					return e.processInitializeException(t).catch((function(t) {
						return e.verifyRideErrorException(t)
					})).catch((function(t) {
						return e.verifyCsrfException(t).catch((function(e) {
							return al.default.info("initialize exception ended", e)
						}))
					}))
				})).then((function(e) {
					i = e
				})).finally((function() {
					return al.default.info("onReady initialization"), window.addEventListener(xl.ASK_FOR_IMSLIB_INSTANCE_DOM_EVENT_NAME, (function() {
						e.triggerOnImsInstance(e)
					}), !1), r.triggerOnReady(i ? i.context : null), e.triggerOnImsInstance(e), e.initialized = !0, Promise.resolve(i)
				}))
			}, e.prototype.notifyParentAboutModalSignIn = function(e) {
				var t = window.location.href.replace("imslibmodal", "wasmodal");
				return window.opener ? (window.opener.postMessage(t, window.location.origin), window.close()) : window["" + e.wndRedirectPropName] = t, Promise.reject("popup")
			}, e.prototype.restoreHash = function() {
				var e = vl.default.fragmentToObject();
				e && e.from_ims && hl.default.setHash(e.old_hash || "")
			}, e.prototype.getTransitoryAuthorizationCode = function(e, t) {
				return void 0 === t && (t = {}), (e = e || {}).response_type = e.response_type || "code", e.target_client_id = e.target_client_id || this.adobeIdData.client_id, e.target_scope = e.target_scope || this.adobeIdData.scope, this.imsApis.getTransitoryAuthorizationCode(e, t, this.adobeIdData.client_id)
			}, e.prototype.jumpToken = function(e, t) {
				return void 0 === t && (t = {}), e.target_client_id = e.target_client_id || this.adobeIdData.client_id, e.target_scope = e.target_scope || this.adobeIdData.scope, this.imsApis.jumpToken(e, t, this.adobeIdData.client_id)
			}, e.prototype.getVerifierByKey = function(e) {
				return (new El.CodeChallenge).getVerifierByKey(e)
			}, e.prototype.socialHeadlessSignIn = function(e, t) {
				return void 0 === t && (t = {}), rl(this, void 0, void 0, (function() {
					var r = this;
					return nl(this, (function(n) {
						return [2, this.imsApis.socialHeadlessSignIn(e, t).then((function(e) {
							return r.exchangeIjt(e.token)
						})).catch((function(e) {
							return "ride_AdobeID_social" === e.error && r.signIn(), Promise.reject(e)
						}))]
					}))
				}))
			}, e
		}(),
		Al = wa.AdobeIMS = Cl;
	class Ol {
		constructor(e) {
			this.initialized = !1, this.imsLocale = "en_US", this.modalMode = !0;
			const t = {
				...e,
				env: this.getEnv(e.env)
			};
			Object.assign(this, t), this.adobeIms = new Al(this.initAdobeImsData)
		}
		get initAdobeImsData() {
			return {
				client_id: this.imsClientId,
				scope: this.imsScope,
				environment: this.env,
				useLocalStorage: !1,
				autoValidateToken: !0,
				redirect_uri: this.redirectUrl,
				onReady: this.onReady.bind(this),
				onError: this.onError.bind(this),
				onAccessToken: this.onAccessToken.bind(this),
				onAccessTokenHasExpired: this.onAccessTokenHasExpired.bind(this),
				onReauthAccessToken: this.onReAuthAccessToken.bind(this),
				locale: this.imsLocale,
				modalMode: this.modalMode,
				modalSettings: this.modalSettings,
				...this.adobeImsOptions
			}
		}
		getEnv(e) {
			return e && "PROD" !== e.toUpperCase() ? Ha.IEnvironment.STAGE : Ha.IEnvironment.PROD
		}
		getAdobeIms() {
			return this.adobeIms
		}
		async initialize() {
			var e;
			await this.adobeIms.initialize(), this.initialized = !0, null === (e = this.onImsServiceInitialized) || void 0 === e || e.call(this, this)
		}
		isSignedInUser() {
			return this.adobeIms.isSignedInUser()
		}
		async triggerAuthFlow() {
			if (!this.isSignedInUser()) return this.signIn()
		}
		getImsToken() {
			var e;
			this.initialized || this.initialize();
			const t = null === (e = this.adobeIms.getAccessToken()) || void 0 === e ? void 0 : e.token;
			return !t && this.imsToken ? this.imsToken : (this.imsToken = t, t)
		}
		setImsToken(e = null) {
			var t;
			this.imsToken = e || (null === (t = this.adobeIms.getAccessToken()) || void 0 === t ? void 0 : t.token)
		}
		async signIn() {
			let e;
			try {
				e = new URL(this.redirectUrl).href
			} finally {
				await this.adobeIms.signIn({
					...e && {
						redirect_uri: e
					}
				})
			}
		}
		async signOut() {
			this.imsToken = void 0, this.adobeIms.signOut()
		}
		async refreshToken() {
			return this.adobeIms.refreshToken()
		}
		async onAccessToken(e) {
			var t;
			this.imsToken = null == e ? void 0 : e.token, null === (t = this.onAccessTokenReceived) || void 0 === t || t.call(this, e)
		}
		onReAuthAccessToken(e) {
			var t;
			this.imsToken = null == e ? void 0 : e.token, null === (t = this.onAccessTokenReceived) || void 0 === t || t.call(this, e)
		}
		async onReady() {
			this.isSignedInUser() || await this.onAccessTokenHasExpired()
		}
		async onAccessTokenHasExpired() {
			var e;
			null === (e = this.onAccessTokenExpired) || void 0 === e || e.call(this)
		}
		onError(e, t) {
			var r;
			null === (r = this.onErrorReceived) || void 0 === r || r.call(this, e, t)
		}
	}
	const Bl = (e, t = undefined, r = !1) => {
			let n = t || (null === window || void 0 === window ? void 0 : window.assetsSelectorsImsService);
			if (!n || r) {
				if (!(null == e ? void 0 : e.imsClientId) || !(null == e ? void 0 : e.imsScope) || !(null == e ? void 0 : e.redirectUrl)) throw new Error("AssetsSelectors: imsClientId, imsScope, redirectUrl are required parameters. Did you forget to pass required props or to call registerAssetSelectorsIms(...)?");
				n = new Ol(e), n.initialize(), window.assetsSelectorsImsService = n
			}
			return n
		},
		Rl = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/,
		Ml = /(?:access_token)\=([\S\s]*?)\&/,
		zl = /(?:client_id)\=([\S\s]*?)\&/,
		Fl = {
			imsClientId: {
				type: De.string,
				defaultValue: ""
			},
			imsScope: {
				type: De.string,
				defaultValue: ""
			},
			redirectUrl: {
				type: De.string,
				defaultValue: ""
			},
			adobeImsOptions: {
				type: De.object,
				defaultValue: {}
			},
			onImsServiceInitialized: {
				type: De.func,
				defaultValue: void 0
			},
			onAccessTokenReceived: {
				type: De.func,
				defaultValue: void 0
			},
			onErrorReceived: {
				type: De.func,
				defaultValue: void 0
			},
			children: {
				type: De.element,
				defaultValue: void 0
			},
			imsTokenServiceInstance: {
				type: De.object,
				defaultValue: void 0
			},
			modalMode: {
				type: De.bool,
				defaultValue: !0
			},
			env: {
				type: De.string,
				defaultValue: "PROD"
			},
			waitForImsToken: {
				type: De.bool,
				defaultValue: !1
			}
		},
		jl = (e, t = undefined) => u.useMemo((() => [Bl(e, t)]), [null == e ? void 0 : e.imsClientId, null == e ? void 0 : e.imsScope, null == e ? void 0 : e.redirectUrl]),
		Ll = e => {
			const {
				children: t,
				imsTokenServiceInstance: r,
				...n
			} = S.PropsUtils.filterInterfaceProps(e, Fl), [o] = jl(n, r), [i, a] = u.useState(""), c = u.useCallback((e => {
				var t, r, i, c, s, u, l, d;
				try {
					if (null === (i = null === (t = new URL(null == o ? void 0 : o.redirectUrl)) || void 0 === t ? void 0 : (r = t.origin).startsWith) || void 0 === i ? void 0 : i.call(r, e.origin)) {
						const t = null === (s = null === (c = null == n ? void 0 : n.adobeImsOptions) || void 0 === c ? void 0 : c.modalSettings) || void 0 === s ? void 0 : s.allowOrigin;
						if (t && !(null === (l = (u = e.origin).startsWith) || void 0 === l ? void 0 : l.call(u, t)) && !Rl.test(null == e ? void 0 : e.data)) return;
						try {
							const t = new URL(null == e ? void 0 : e.data).hash;
							if ((null === (d = t.match(zl)) || void 0 === d ? void 0 : d[1]) === (null == o ? void 0 : o.imsClientId)) {
								const e = t.match(Ml)[1];
								o.setImsToken(e), window.assetsSelectorsImsService = o, a(e)
							}
						} catch (e) {}
					}
				} catch (e) {}
			}), []);
			return u.useEffect((() => {
				(async () => {
					var e, t, r;
					return await (null === (e = o.initialize) || void 0 === e ? void 0 : e.call(o)), await (null === (t = o.triggerAuthFlow) || void 0 === t ? void 0 : t.call(o)), (null === (r = o.getImsToken) || void 0 === r ? void 0 : r.call(o)) || (null == o ? void 0 : o.imsToken)
				})().then((e => {
					var t;
					null === (t = o.setImsToken) || void 0 === t || t.call(o, e), window.assetsSelectorsImsService = o, a(e)
				}))
			}), []), u.useEffect((() => (window.addEventListener("message", c, !1), () => {
				window.removeEventListener("message", c)
			})), []), h.cloneElement(t, {
				waitForImsToken: !0,
				imsToken: i,
				imsTokenService: o,
				...n
			})
		},
		Ul = e => t => {
			const r = (e => {
				const [t] = jl(e), [r, n] = u.useState(""), o = u.useMemo((() => r => {
					const n = {
						...e,
						...r,
						waitForImsToken: !0
					};
					return h.createElement(Ll, {
						imsTokenServiceInstance: t,
						...n
					})
				}), [null == e ? void 0 : e.imsClientId, null == e ? void 0 : e.imsScope, null == e ? void 0 : e.redirectUrl]);
				return u.useEffect((() => {
					(async () => t.getImsToken())().then((e => n(e)))
				}), []), {
					imsToken: r,
					imsTokenService: t,
					ImsSusiFlow: o
				}
			})(t);
			return h.createElement(r.ImsSusiFlow, null, h.createElement(e, {
				...t
			}))
		},
		Nl = (e, t, r) => {
			T.render(e, t, r)
		};

	function Hl(e) {
		! function(e, t, r) {
			if (void 0 === r && (r = Error), !e) throw new r(t)
		}(e, "[React Intl] Could not find required `intl` object. <IntlProvider> needs to exist in the component ancestry.")
	}
	Ut(Ut({}, {
		formats: {},
		messages: {},
		timeZone: void 0,
		defaultLocale: "en",
		defaultFormats: {},
		fallbackOnEmptyString: !0,
		onError: function(e) {},
		onWarn: function(e) {}
	}), {
		textComponent: u.Fragment
	});
	var Vl = u.createContext(null);
	Vl.Consumer, Vl.Provider;
	var ql, Wl, Gl = Vl;

	function Kl() {
		var e = u.useContext(Gl);
		return Hl(e), e
	}

	function $l(e) {
		var t = function(t) {
			var r = Kl(),
				n = t.value,
				o = t.children,
				i = Nt(t, ["value", "children"]),
				a = "string" == typeof n ? new Date(n || 0) : n;
			return o("formatDate" === e ? r.formatDateToParts(a, i) : r.formatTimeToParts(a, i))
		};
		return t.displayName = Wl[e], t
	}

	function Ql(e) {
		var t = function(t) {
			var r = Kl(),
				n = t.value,
				o = t.children,
				i = Nt(t, ["value", "children"]),
				a = r[e](n, i);
			if ("function" == typeof o) return o(a);
			var c = r.textComponent || u.Fragment;
			return u.createElement(c, null, a)
		};
		return t.displayName = ql[e], t
	}

	function Xl(e, t) {
		return function(e) {
			if (Array.isArray(e)) return e
		}(e) || function(e, t) {
			var r = null == e ? null : "undefined" != typeof Symbol && e[Symbol.iterator] || e["@@iterator"];
			if (null == r) return;
			var n, o, i = [],
				a = !0,
				c = !1;
			try {
				for (r = r.call(e); !(a = (n = r.next()).done) && (i.push(n.value), !t || i.length !== t); a = !0);
			} catch (e) {
				c = !0, o = e
			} finally {
				try {
					a || null == r.return || r.return()
				} finally {
					if (c) throw o
				}
			}
			return i
		}(e, t) || function(e, t) {
			if (!e) return;
			if ("string" == typeof e) return Yl(e, t);
			var r = Object.prototype.toString.call(e).slice(8, -1);
			"Object" === r && e.constructor && (r = e.constructor.name);
			if ("Map" === r || "Set" === r) return Array.from(e);
			if ("Arguments" === r || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(r)) return Yl(e, t)
		}(e, t) || function() {
			throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")
		}()
	}

	function Yl(e, t) {
		(null == t || t > e.length) && (t = e.length);
		for (var r = 0, n = new Array(t); r < t; r++) n[r] = e[r];
		return n
	}! function(e) {
		e.formatDate = "FormattedDate", e.formatTime = "FormattedTime", e.formatNumber = "FormattedNumber", e.formatList = "FormattedList", e.formatDisplayName = "FormattedDisplayName"
	}(ql || (ql = {})),
	function(e) {
		e.formatDate = "FormattedDateParts", e.formatTime = "FormattedTimeParts", e.formatNumber = "FormattedNumberParts", e.formatList = "FormattedListParts"
	}(Wl || (Wl = {})), Ql("formatDate"), Ql("formatTime"), Ql("formatNumber"), Ql("formatList"), Ql("formatDisplayName"), $l("formatDate"), $l("formatTime");
	var Jl = {
			columnLabelName: {
				id: "AssetTableViewColumns.ColumnLabelName",
				defaultMessage: "Name",
				description: "Default label for column"
			},
			columnLabelFormat: {
				id: "AssetTableViewColumns.ColumnLabelFormat",
				defaultMessage: "Format",
				description: "Default label for column"
			},
			columnLabelDimensions: {
				id: "AssetTableViewColumns.ColumnLabelDimensions",
				defaultMessage: "Dimensions",
				description: "Default label for column"
			},
			columnLabelSize: {
				id: "AssetTableViewColumns.ColumnLabelSize",
				defaultMessage: "Size",
				description: "Default label for column"
			},
			columnLabelModified: {
				id: "AssetTableViewColumns.ColumnLabelModified",
				defaultMessage: "Modified",
				description: "Default label for column"
			},
			columnLabelStatus: {
				id: "AssetTableViewColumns.ColumnLabelStatus",
				defaultMessage: "Status",
				description: "Default label for column"
			}
		},
		Zl = {
			title: "",
			sortable: !1,
			key: "info",
			value: "info",
			width: 0,
			maxWidth: 0
		},
		ed = [{
			title: "",
			key: "thumbnail",
			width: 70,
			announce: !1,
			sortable: !1
		}, function(e) {
			return {
				title: e(Jl.columnLabelName),
				key: "name",
				minWidth: 180,
				announce: !1,
				sortable: !0,
				value: "name"
			}
		}, function(e) {
			return {
				title: e(Jl.columnLabelStatus),
				key: "reviewStatus",
				minWidth: 100,
				maxWidth: 160,
				sortable: !1,
				formatMessage: e
			}
		}, function(e) {
			return {
				title: e(Jl.columnLabelFormat),
				key: "mimetype",
				minWidth: 80,
				maxWidth: 160,
				sortable: !1,
				value: "format"
			}
		}, function(e) {
			return {
				title: e(Jl.columnLabelDimensions),
				key: "dimension",
				minWidth: 110,
				maxWidth: 160,
				sortable: !1
			}
		}, function(e) {
			return {
				title: e(Jl.columnLabelSize),
				key: "bytesize",
				minWidth: 80,
				maxWidth: 100,
				sortable: !1,
				value: "size"
			}
		}, function(e) {
			return {
				title: e(Jl.columnLabelModified),
				key: "lastModifiedDate",
				minWidth: 117,
				maxWidth: 200,
				sortable: !1,
				value: "modified"
			}
		}, {
			title: "",
			key: "actions",
			width: 70,
			sortable: !1,
			value: "actions"
		}];

	function td(e, t) {
		return e.map((function(e) {
			return "function" == typeof e ? e(t) : e
		}))
	}
	var rd = td(ed, (function(e) {
		return e.defaultMessage
	}));
	var nd = {
			extractDefaultFilters: function(e) {
				var t = {};
				return null == e || e.forEach((function(e) {
					var r = e.fields,
						n = e.header;
					r.forEach((function(e) {
						var r = e.defaultValue,
							o = void 0 === r ? [] : r,
							i = e.options,
							a = e.name,
							c = e.element,
							s = i.map((function(e) {
								var t = e.value,
									r = e.label;
								return o.includes(t) ? {
									label: r,
									filter: t,
									type: c,
									header: n
								} : null
							})).filter(Boolean);
						s.length && (t[a] = s)
					}))
				})), t
			}
		},
		od = "folder",
		id = "library",
		ad = "image",
		cd = "video",
		sd = "unsupported",
		ud = function(e) {
			if (e) {
				if (e.startsWith("image")) return ad;
				if (0 === e.indexOf("application/vnd.adobecloud.directory+json") || 0 === e.indexOf("application/x-sharedcloud-collection+json")) return od;
				if (e.startsWith("video") && "video/quicktime" !== e) return cd;
				if (e.indexOf("library") > -1) return id
			}
			return sd
		},
		ld = function(e) {
			var t = arguments.length > 1 && void 0 !== arguments[1] ? arguments[1] : 2;
			if (!e || 0 === e || isNaN(e)) return "0 Bytes";
			var r = t < 1 ? 1 : t,
				n = Math.floor(Math.log(e) / Math.log(1e3));
			return "".concat(parseFloat((e / Math.pow(1e3, n)).toPrecision(r)), " ").concat(["Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"][n])
		},
		dd = function(e) {
			var t = e["tiff:imageWidth"],
				r = e["tiff:imageLength"];
			return ud(e["dc:format"]) !== od && (t || r) ? "".concat(e["tiff:imageWidth"] || "?", " x ").concat(e["tiff:imageLength"] || "?", " px") : ""
		},
		pd = {
			infoMissingLabel: {
				id: "SelectorBrowse.InfoPopover.MissingLabel",
				defaultMessage: "Unknown",
				description: "Label for missing info"
			},
			infoMissingDescriptionLabel: {
				id: "SelectorBrowse.InfoPopover.MissingDescriptionLabel",
				defaultMessage: "(no description)",
				description: "Label for a missing description"
			},
			infoPopoverDimensionLabel: {
				id: "SelectorBrowse.InfoPopover.DimensionLabel",
				defaultMessage: "Dimensions",
				description: "Default label for asset dimension"
			},
			infoPopoverSizeLabel: {
				id: "SelectorBrowse.InfoPopover.SizeLabel",
				defaultMessage: "Size",
				description: "Default label for asset size"
			},
			infoPopoverTagsLabel: {
				id: "SelectorBrowse.InfoPopover.TagsLabel",
				defaultMessage: "Tags",
				description: "Default label for asset tags"
			},
			infoPopoverDescriptionLabel: {
				id: "SelectorBrowse.InfoPopover.DescriptionLabel",
				defaultMessage: "Description",
				description: "Default label for assets description"
			},
			infoPopoverDateCreated: {
				id: "SelectorBrowse.InfoPopover.DateCreated",
				defaultMessage: "Date Created",
				description: "Default label for date created"
			},
			infoPopoverDateModified: {
				id: "SelectorBrowse.InfoPopover.DateModified",
				defaultMessage: "Date Modified",
				description: "Default label for date modified"
			},
			infoPopoverPath: {
				id: "SelectorBrowse.InfoPopover.Path",
				defaultMessage: "Path",
				description: "Default label for path label"
			},
			infoPopoverFolderLabel: {
				id: "SelectorBrowse.InfoPopover.FolderLabel",
				defaultMessage: "Folder",
				description: "Default label for folder"
			},
			infoPopoverImageLabel: {
				id: "SelectorBrowse.InfoPopover.ImageLabel",
				defaultMessage: "Image",
				description: "Default label for image"
			},
			infoPopoverLibraryLabel: {
				id: "SelectorBrowse.InfoPopover.LibraryLabel",
				defaultMessage: "Library",
				description: "Default label for library"
			}
		},
		fd = {
			formatMessage: function(e) {
				return e.defaultMessage
			}
		},
		md = function(e, t, r) {
			switch (ud(e)) {
				case od:
					return t.formatMessage(r.infoPopoverFolderLabel);
				case id:
					return t.formatMessage(r.infoPopoverLibraryLabel);
				default:
					return e || t.formatMessage(r.infoMissingLabel)
			}
		},
		gd = function(e) {
			return e ? new Date(e).toDateString() : ""
		};
	e.FilterUtils = nd, e.TableColumns = rd, e.getColumnByValue = function() {
		var e = arguments.length > 1 && void 0 !== arguments[1] ? arguments[1] : "";
		return Xl((arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : []).filter((function(t) {
			return t.value === e
		})), 1)[0]
	}, e.getDefaultInfoPopoverData = function(e) {
		var t = arguments.length > 1 && void 0 !== arguments[1] ? arguments[1] : fd,
			r = arguments.length > 2 && void 0 !== arguments[2] ? arguments[2] : pd;
		return [{
			value: e["repo:name"],
			label: md(e["dc:format"], t, r),
			renderType: "title"
		}, {
			value: dd(e),
			label: t.formatMessage(r.infoPopoverDimensionLabel),
			renderType: "col",
			defaultValue: t.formatMessage(r.infoMissingLabel)
		}, {
			value: gd(e["repo:modifyDate"]),
			label: t.formatMessage(r.infoPopoverDateModified),
			renderType: "col",
			defaultValue: t.formatMessage(r.infoMissingLabel)
		}, {
			value: ld(e["repo:size"]),
			label: t.formatMessage(r.infoPopoverSizeLabel),
			renderType: "col",
			defaultValue: t.formatMessage(r.infoMissingLabel)
		}, {
			value: gd(e["repo:createDate"]),
			label: t.formatMessage(r.infoPopoverDateCreated),
			renderType: "col",
			defaultValue: t.formatMessage(r.infoMissingLabel)
		}, {
			value: e["dc:description"],
			label: t.formatMessage(r.infoPopoverDescriptionLabel),
			renderType: "row",
			defaultValue: t.formatMessage(r.infoMissingDescriptionLabel)
		}, {
			value: e["repo:path"],
			label: t.formatMessage(r.infoPopoverPath),
			renderType: "row",
			defaultValue: t.formatMessage(r.infoMissingLabel)
		}]
	}, e.getGeneralTableViewColumns = function(e) {
		return td([].concat(ed, [Zl]), e)
	}, e.registerAssetSelectorsIms = (e, t) => Bl(e, null, t), e.renderAssetSelector = (e, t, r) => {
		Nl(h.createElement(Ke, {
			...t
		}), e, r)
	}, e.renderAssetSelectorWithIms = (e, t, r) => {
		const n = Ul($e);
		Nl(h.createElement(n, {
			...t
		}), e, r)
	}, e.renderDestinationSelector = (e, t, r) => {
		Nl(h.createElement(ya, {
			...t
		}), e, r)
	}, e.renderDestinationSelectorWithIms = (e, t, r) => {
		const n = Ul(_a);
		Nl(h.createElement(n, {
			...t
		}), e, r)
	}
}));
//# sourceMappingURL=asset-selectors.js.map
